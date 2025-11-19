export class ApplicationScore {
  id: number;
  score: number;
}

export class AIRankingResponse {
  id: string;
  applications: ApplicationScore[];
}
