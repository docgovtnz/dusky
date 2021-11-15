import { Params, DefaultUrlSerializer } from '@angular/router';
import * as moment from 'moment';

export abstract class AbstractCriteria {
  pageNumber = 1;
  pageSize = 100;

  constructor() {}

  protected doPopulateFromParams(
    params: Params,
    booleanProperties: string[],
    integerProperties: string[],
    dateProperties: string[],
    stringArrayProperties: string[]
  ) {
    // deserialise the parameters and copy them to the criteria object
    Object.keys(params).forEach((key) => {
      if (params[key]) {
        if (booleanProperties.includes(key)) {
          // booleans that come from strings (i.e. queryParams) need to be revived back into booleans
          this[key] = params[key] === 'true';
        } else if (integerProperties.includes(key)) {
          // int that come from strings (i.e. queryParams) need to be revived back into ints
          this[key] = parseInt(params[key], 10);
        } else if (dateProperties.includes(key)) {
          // Date that come from strings (i.e. queryParams) need to be revived back into Date objects
          this[key] = new Date(params[key]);
        } else if (stringArrayProperties.includes(key)) {
          if (typeof params[key] === 'string') {
            this[key] = [params[key]];
          } else {
            console.log(typeof params[key]);
            this[key] = params[key];
          }
        } else {
          this[key] = params[key];
        }
      } else {
        this[key] = null;
      }
    });
  }

  doToParams(self: any): Params {
    const criteria = {};
    // serialise the criteria
    Object.keys(self).forEach((key) => {
      const value = self[key];
      if (value !== null) {
        if (value instanceof Date) {
          // Dates that go outbound as strings (i.e. queryParams) need to be in the right format or deleted from the object
          criteria[key] = moment(value).toISOString();
        } else {
          criteria[key] = value;
        }
      } else {
        // use empty string to represent null
        criteria[key] = '';
      }
    });
    return criteria;
  }

  abstract toParams(): Params;

  abstract populateFromParams(params: Params);

  /**
   * Converts this criteria to a url string given the supplied base url.
   *
   * Note this will add a starting slash
   *
   * @param baseUrl cannot be empty or null
   */
  toUrl(baseUrl: string) {
    if (!baseUrl || baseUrl === '' || !baseUrl.startsWith('/')) {
      throw Error('baseUrl cannot be empty or null and must start with \'/\'');
    }
    // turn the record criteria into a url using underlying url serialisation behind router
    const urlSerializer = new DefaultUrlSerializer();
    // start by creating the base url tree
    const urlTree = urlSerializer.parse(baseUrl);
    urlTree.queryParams = this.toParams();
    // convert to url string
    const url = urlSerializer.serialize(urlTree);
    return url;
  }
}
