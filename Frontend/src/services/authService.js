// Login user
export async function loginUser(email, password) {
  const response = await fetch("/api/auth/login", {
    method: "POST",

    headers: {
      "Content-Type": "application/json",
    },

    body: JSON.stringify({
      email,
      password,
    }),
  });

  if (!response.ok) {
    throw new Error("Invalid email or password");
  }

  return response.json();
}
// Get current user role and permissions
export async function getCurrentUser(token) {
  const response = await fetch("/api/auth/me", {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    throw new Error("Unable to load current user");
  }

  return response.json();
}