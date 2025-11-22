import { IsString, IsOptional, IsNotEmpty } from 'class-validator';

export class CorrectCvDto {
  @IsString()
  @IsNotEmpty()
  cvContent: string;

  @IsString()
  @IsOptional()
  jobDescription?: string;
}
