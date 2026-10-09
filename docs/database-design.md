# Database Design (Supabase PostgreSQL)

## Initial Entities

### `users`
Stores all registered users across the platform.
* `id` (INT, PK)
* `name` (VARCHAR)
* `email` (VARCHAR, UNIQUE)
* `password` (VARCHAR)
* `role` (ENUM: ADMIN, INSTRUCTOR, STUDENT)
* `created_at` (TIMESTAMP)

## Future Entities
The following tables are planned for future phases:
* `exercises`: Coding challenges and problem statements.
* `submissions`: User code submissions for exercises.
* `progress`: Tracking of solved exercises per student.
* `messages`: System and user-to-user messages.
* `settings`: User preferences and platform settings.
* `challenges`: Time-bound or advanced coding competitions.
