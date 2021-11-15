import { ValidationService } from '../validation.service';
import { ValidationErrors } from '@angular/forms';

export class ValidationResultFactory {
  messageTemplates: any;
  validationErrors: ValidationErrors = {};

  constructor(private ruleName, private validationService: ValidationService) {
    this.validationService.messageTemplateMap$.subscribe(
      (messageTemplateMap) => {
        this.messageTemplates = messageTemplateMap.map[ruleName];
      }
    );
  }

  addResult(key: string): any {
    if (this.messageTemplates) {
      const messageTemplate = this.messageTemplates.map[key]; // worried about a race condition here between this and the constructor
      this.validationErrors[key] = messageTemplate.messageText;
    }
  }

  addResultWithParams(key: string, params: { [key: string]: any }): any {
    if (this.messageTemplates) {
      const messageTemplate = this.messageTemplates.map[key]; // worried about a race condition here between this and the constructor
      let messageText = messageTemplate.messageText as string;
      Object.keys(params).forEach((paramKey) => {
        messageText = messageText.replace(
          new RegExp('{' + paramKey + '}', 'g'),
          params[paramKey]
        );
      });

      this.validationErrors[key] = messageText;
    }
  }
}
