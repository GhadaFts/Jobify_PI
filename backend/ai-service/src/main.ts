import { NestFactory } from '@nestjs/core';
import { ValidationPipe } from '@nestjs/common';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  // COMMENTEZ OU SUPPRIMEZ cette section CORS
  // app.enableCors({
  //   origin: 'http://localhost:4200',
  //   methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
  //   credentials: true,
  // });

  app.useGlobalPipes(new ValidationPipe());
  const port = process.env.PORT || 3001;
  await app.listen(port);
  console.log(`AI Service is running on port ${port}`);
}
bootstrap();
