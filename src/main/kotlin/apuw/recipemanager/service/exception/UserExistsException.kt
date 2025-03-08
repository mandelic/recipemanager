package apuw.recipemanager.service.exception

class UserExistsException(username: String) : RuntimeException("User with username $username already exists.")
