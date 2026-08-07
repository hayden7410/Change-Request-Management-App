import { apiFetch } from "./api";

export function getDepartments() {
  return apiFetch("/api/departments");
}