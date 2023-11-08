import { FileHandle } from "./file-handle.model";

export interface Review {
  id?: string; // MongoDB document ID
  clientId: string,
  reviewId: string,
  productName: string;
  firstName: string;
  lastName: string;
  email: string,
  zipCode: number; 
  rateCode: number;
  comments: string;
  dateTime: string;
  reviewImages: FileHandle[]
  reviewImage: string;
}
