export class ReportNoraNetErrorDto {
  id: string;
  dateProcessed: Date;
  fileName: string;
  fileData: string;
  message: string;
  dataImported: boolean;
  actioned: boolean;
}
