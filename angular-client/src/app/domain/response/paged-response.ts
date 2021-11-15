export class PagedResponse {
  page: number;
  pageSize: number;
  total: number;
  numberOfPages: number;
  results: any[];

  firstResult: number;
  lastResult: number;

  constructor() {}
}
