import { Observable, Subject, timer, of } from 'rxjs';
import {
  switchMap,
  shareReplay,
  startWith,
  tap,
  takeUntil,
  catchError,
  filter,
  take,
} from 'rxjs/operators';
import { isDevMode } from '@angular/core';
import * as moment from 'moment';

export class Cache<T> {
  // Name of the cache to support logging.
  cacheName: string;

  // Has this been primed at all?
  primed = false;

  // Subject that we can use to trigger a forced/immediate refresh.
  $forceRefresh = new Subject<void>();

  // Subject to force the cache to stop.
  $stop = new Subject<void>();

  $refresh = new Subject<Cache<any>>();

  refreshPeriodMillis$: Observable<number>;

  fn: () => Observable<T>;

  // The observable for the data.
  $data: Observable<T>;

  constructor(
    name: string,
    refreshPeriodMillis$: Observable<number>,
    fn: () => Observable<T>
  ) {
    // Store the name for logging.
    this.cacheName = name;
    this.refreshPeriodMillis$ = refreshPeriodMillis$;
    this.fn = fn;

    // Cannot initiate the caches in the constructor as this will hit security issues.
    //this.initCache();
  }

  initCache() {
    // Create the cache. Listen for the force refresh.
    this.$data = this.$forceRefresh.pipe(
      // Start with an immediate run (i.e. don't wait for a signal from forceRefresh to run).
      startWith(null),
      tap(() => (this.primed = true)),
      // Map to a timer. The start time is randomized to offset multiple caches created at the
      // same time, but we use startWith(0) to force an immediate invocation to start with (ensuring that
      // a forceRefresh takes effect instantly). Each time forceRefresh is called, switchMap starts
      // a new timer and discards the last one.
      switchMap(() =>
        this.refreshPeriodMillis$.pipe(
          switchMap((refreshPeriodMillis) =>
            timer(
              refreshPeriodMillis / 2 +
                Math.floor((refreshPeriodMillis / 2) * Math.random()),
              refreshPeriodMillis
            ).pipe(startWith(0))
          )
        )
      ),
      // Log a refresh message.
      tap(() => {
        this.debug('Refreshing cache');
      }),
      // Retrieve the data.
      switchMap(() =>
        this.fn().pipe(
          // Ignore errors. We will get retry based on the timer.
          catchError(() => {
            this.debug('Error detected - will retry on next refresh');
            return of(null);
          }),
          filter((val) => val !== null)
        )
      ),
      tap(() => this.$refresh.next(this)),
      // Listen for signal to stop the refresh.
      takeUntil(this.$stop),
      // Keep the data for replay so new subscribers can re-use the current value.
      shareReplay(1)
    );
  }

  debug(message: string) {
    if (isDevMode()) {
      console.log(
        '[' +
          moment().format('DD/MM/YYYY HH:mm:ss.SSS') +
          '] [Cache - ' +
          this.cacheName +
          '] - ' +
          message
      );
    }
  }

  /**
   * Called to retrieve the cached data. It may be desirable to pipe through take(1) if the consumer
   * does not want to see automatically updating data when the cache is refreshed.
   */
  get(): Observable<T> {
    if (!this.$data) {
      this.debug('Re-initialising the cache');
      this.initCache();
    }
    return this.$data;
  }

  /**
   * Force the data to refresh immediately. Note that this does NOT restat the timer - i.e. if the timer
   * had 5 seconds left, the cache will still refresh after 5 seconds.
   */
  forceRefresh() {
    if (!this.primed) {
      this.debug('Not primed - creating a dummy subscription');
      this.get().pipe(take(1)).subscribe();
    } else {
      this.debug('Primed - forcing a refresh');
      this.$forceRefresh.next();
    }
  }

  stop() {
    this.debug('Stopping cache refresh');
    this.$stop.next();
    this.$data = null;
    this.primed = false;
    //this.$stop.complete();
  }
}
