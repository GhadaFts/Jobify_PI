import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import { promises as fs } from 'fs';
import { join } from 'path';
import { v4 as uuidv4 } from 'uuid';

@Injectable()
export class UploadService {
  private readonly uploadPath = join(process.cwd(), 'uploads', 'profile-photos');

  constructor() {
    this.ensureUploadDirectory();
  }

  private async ensureUploadDirectory() {
    try {
      await fs.mkdir(this.uploadPath, { recursive: true });
    } catch (error) {
      console.error('Failed to create upload directory:', error);
    }
  }

  async saveImage(file: Express.Multer.File): Promise<string> {
    try {
      // Generate unique filename
      const fileExtension = file.originalname.split('.').pop();
      const filename = `${uuidv4()}.${fileExtension}`;
      const filepath = join(this.uploadPath, filename);

      // Save file
      await fs.writeFile(filepath, file.buffer);

      // Return URL path (not filesystem path)
      return `/uploads/profile-photos/${filename}`;
    } catch (error) {
      throw new HttpException('Failed to save image', HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  async deleteImage(imageUrl: string): Promise<void> {
    try {
      // Extract filename from URL
      const filename = imageUrl.split('/').pop();
      if (!filename) return;

      const filepath = join(this.uploadPath, filename);

      // Check if file exists
      await fs.access(filepath);

      // Delete file
      await fs.unlink(filepath);
    } catch (error) {
      // File doesn't exist or couldn't be deleted - not critical
      console.warn('Failed to delete image:', error.message);
    }
  }
}