# Recipe Manager API

This is a **Recipe Manager API** that allows you to manage recipes, components, steps, ingredients, and users. You can perform CRUD (Create, Read, Update, Delete) operations on recipes, components, steps, ingredients, and user accounts.

## Endpoints Overview

### **Recipes Management**
- **GET** `/api/recipes/{id}`  
  Get a recipe by its ID.

- **PUT** `/api/recipes/{id}`  
  Update an existing recipe.

- **DELETE** `/api/recipes/{id}`  
  Delete a recipe by its ID.

- **GET** `/api/recipes`  
  Get all recipes.

- **POST** `/api/recipes`  
  Add a new recipe.

- **GET** `/api/recipes/{id}/components`  
  Get components of a specific recipe.

- **POST** `/api/recipes/{id}/components`  
  Add a new component to a recipe.

---

### **Component Management**
- **PUT** `/api/components/{id}`  
  Update a specific component.

- **DELETE** `/api/components/{id}`  
  Delete a component by its ID.

- **GET** `/api/components/{id}/steps`  
  Get steps of a specific component.

- **POST** `/api/components/{id}/steps`  
  Add a new step to a component.

- **GET** `/api/components/{id}/ingredients`  
  Get ingredients of a specific component.

- **POST** `/api/components/{id}/ingredients`  
  Add a new ingredient to a component.

---

### **User Management**
- **GET** `/api/users/{id}`  
  Get user information by user ID.

- **PUT** `/api/users/{id}`  
  Update user information by user ID.

- **DELETE** `/api/users/{id}`  
  Delete a user by user ID.

- **GET** `/api/users`  
  Get all users.

---

### **Authentication**
- **POST** `/api/auth/register`  
  Register a new user.

- **POST** `/api/auth/login`  
  Login an existing user.

---

### **Steps and Ingredients Management**
- **DELETE** `/api/steps/{id}`  
  Delete a step by its ID.

- **DELETE** `/api/ingredients/{id}`  
  Delete an ingredient by its ID.

---

## How to Use

1. **Authentication**:  
   Before using most endpoints, you must register or log in a user. After authentication, you will be able to access restricted endpoints.

2. **Managing Recipes**:  
   You can create, update, delete, or fetch recipes. You can also add and manage components (steps and ingredients) for each recipe.

3. **Managing Users**:  
   The API allows you to manage users (retrieve, update, and delete user data).

---
## PostgreSQL Setup

The Recipe Manager API requires a **PostgreSQL** database for storing recipes, components, users, and related information.

To set up PostgreSQL:

1. Download and install PostgreSQL from [here](https://www.postgresql.org/download/).
2. Create a database named recipes_db or modify the application.properties file to point to your existing database.
3. Ensure the connection credentials (username, password, and database URL) are correctly set in your application.properties.

## Roles and Permissions

The Recipe Manager API uses role-based access control (RBAC) to restrict access to certain endpoints based on user roles. There are two primary roles in the system:

### **ADMIN Role**
- **Permissions**:
    - Can access and manage all users.
    - Can retrieve, update, and delete any user in the system.
    - Can manage system-wide settings and perform administrative tasks.

- **Access**:
    - Access to user management endpoints, including the ability to delete users or update user details.
    - Access to all system functionalities that require administrative privileges.

### **USER Role**
- **Permissions**:
    - Can view and update their own user information.
    - Can access their own personal data but cannot delete or manage other users.

- **Access**:
    - Access to certain user-related endpoints such as viewing their personal information and updating their profile.

### **Access Control**:
- Only **ADMIN** users can delete other users or perform administrative tasks.
- Both **ADMIN** and **USER** roles can retrieve and update their own user details (except for user deletion).
- Unauthorized users will receive a **403 Forbidden** response when attempting to access restricted endpoints.

