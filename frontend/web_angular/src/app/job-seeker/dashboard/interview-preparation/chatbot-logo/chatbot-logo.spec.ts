import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatbotLogo } from './chatbot-logo';

describe('ChatbotLogo', () => {
  let component: ChatbotLogo;
  let fixture: ComponentFixture<ChatbotLogo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChatbotLogo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChatbotLogo);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
