import { apiFetch } from "./api";

export function getMyRequests(status = null) {
  let url = "/api/change-requests/mine";

  if (status) {
    url += `?status=${status}`;
  }

  return apiFetch(url);
}

export function getChangeRequestById(id) {
  return apiFetch(`/api/change-requests/${id}`);
}

export function getStatusHistory(id) {
  return apiFetch(`/api/change-requests/${id}/history`);
}

export function getComments(id) {
  return apiFetch(`/api/change-requests/${id}/comments`);
}

export function addComment(id, content) {
  return apiFetch(`/api/change-requests/${id}/comments`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      content,
    }),
  });
}
export function createChangeRequest(request) {
  return apiFetch("/api/change-requests", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });
}
export function updateDraft(id, request) {
  return apiFetch(`/api/change-requests/${id}`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });
}