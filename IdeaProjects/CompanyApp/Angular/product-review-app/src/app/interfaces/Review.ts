export interface Review {
  id?: string; // MongoDB document ID
  reviewId: string,
  productName: string;
  firstName: string;
  lastName: string;
  zipCode: number; 
  rateCode: number;
  comments: string;
  dateTime: string;
}
