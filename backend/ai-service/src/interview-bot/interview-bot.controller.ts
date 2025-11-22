import { Controller, Post, Body, HttpCode, HttpStatus } from '@nestjs/common';
import { InterviewBotService } from './interview-bot.service';
import { ChatRequestDto } from './dto/chat-request.dto';
import { ChatResponseDto } from './dto/chat-response.dto';

@Controller('interview-bot')
export class InterviewBotController {
  constructor(private readonly interviewBotService: InterviewBotService) {}

  @Post('chat')
  @HttpCode(HttpStatus.OK)
  async handleChat(
    @Body() chatRequest: ChatRequestDto,
  ): Promise<ChatResponseDto> {
    return this.interviewBotService.handleChat(chatRequest);
  }
}
