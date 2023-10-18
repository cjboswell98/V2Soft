export interface Review {
  id?: string; // MongoDB document ID (assuming it's a string)
  productName: string;
  firstName: string;
  lastName: string;
  zipCode: number; // Assuming this is a number
  rateCode: number; // Assuming this is a number
  comments: string;
  dateTime: string; // Assuming this is a string representing date and time
}
