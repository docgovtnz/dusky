import { Injectable } from '@angular/core';

const CACHE_LIMIT = 15;

@Injectable()
export class SearchCacheService {
  private cacheArray: any[] = [];

  get(url: string): any {
    const item = this.cacheArray.find((i) => i.key === url);
    if (item) {
      return item.value;
    } else {
      return null;
    }
  }

  put(url: string, results: any) {
    this.cacheArray.push({ key: url, value: results });
    // TODO put cache side into configuration
    while (this.cacheArray.length > CACHE_LIMIT) {
      this.cacheArray.shift();
    }
  }

  clear(baseUrl: string) {
    this.cacheArray = this.cacheArray.filter(
      (item) => !item.key.startsWith(baseUrl)
    );
  }
}
