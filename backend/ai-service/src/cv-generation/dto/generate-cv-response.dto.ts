export class GeneratedCVSection {
  title!: string;
  content!: string;
  order!: number;
}

export class GenerateCVResponse {
  sections!: GeneratedCVSection[];
  summary!: string;
  optimizedSkills!: string[];
  atsScore!: number;
  keywords!: string[];
  rawContent!: string;
}
