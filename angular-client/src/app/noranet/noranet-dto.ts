export class NoraNetDto {
  // From NoraNetSearchDTO.java
  activity: number;
  activityDate: Date;
  batteryLife: number;
  batteryVolts: number;
  birdList: any[];
  category: string;
  cmFemaleList: any[];
  cmHour: number;
  cmMinute: number;
  dataType: string;
  daysSinceChange: number;
  incubating: boolean;
  island: string;
  lastCmHour: number;
  lastCmMinute: number;
  matingAge: number;
  noraNetId: string;
  peakTwitch: number;
  pulseCount: number;
  stationId: string;
  uhfId: number;
}
