# MCP GraphQL Server

Повноцінна реалізація MCP (Model Context Protocol) сервера з підтримкою Streamable-HTTP транспорту та GraphQL бекенду для управління авторами та книгами.

## Про проект

Цей проект демонструює:

- **MCP (Model Context Protocol)** - протокол для комунікації між AI моделями та зовнішніми сервісами
- **Streamable-HTTP транспорт** - один з транспортних протоколів MCP для HTTP-based комунікації
- **GraphQL** - мова запитів для API з гнучкою схемою даних
- **CRUD операції** - повний набір операцій створення, читання, оновлення та видалення
- **Many-to-Many зв'язки** - відношення між авторами та книгами

## Архітектура

```
┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐
│   MCP Client        │────│   MCP Server        │────│   GraphQL Layer     │
│   (Java Client)     │    │   (Spring Boot)     │    │   (Resolvers)       │
└─────────────────────┘    └─────────────────────┘    └─────────────────────┘
                                       │                           │
                           ┌─────────────────────┐    ┌─────────────────────┐
                           │   MCP Tools         │    │   JPA Entities      │
                           │   (CRUD Operations) │    │   (Author, Book)    │
                           └─────────────────────┘    └─────────────────────┘
                                       │                           │
                                       └───────────────────────────┘
                                                    │
                                       ┌─────────────────────┐
                                       │   Postres Database  │
                                       │                     │
                                       └─────────────────────┘
```

## Ключові особливості

### MCP (Model Context Protocol)

MCP - це відкритий стандарт для безпечного підключення AI систем до зовнішніх даних та інструментів. Основні переваги:

- **Стандартизація**: Єдиний протокол для різних AI систем
- **Безпека**: Контрольований доступ до зовнішніх ресурсів
- **Гнучкість**: Підтримка різних транспортів та форматів даних

### Транспорти MCP

1. **stdio** - комунікація через стандартні потоки вводу/виводу
2. **SSE (Server-Sent Events)** - односпрямований потік даних від сервера
3. **Streamable-HTTP** - двосторонній HTTP-based транспорт (використовується в проекті)

### GraphQL переваги

- **Гнучкі запити**: Клієнт запитує тільки потрібні дані
- **Сильна типізація**: Автоматична валідація та документація
- **Єдина точка входу**: Один endpoint для всіх операцій

## Структура проекту

```
src/main/java/com/example/mcp/
├── entity/                 # JPA сутності
│   ├── Author.java         # Сутність автора
│   └── Book.java           # Сутність книги
├── repository/             # JPA репозиторії
│   ├── AuthorRepository.java
│   └── BookRepository.java
├── graphql/               # GraphQL резолвери
│   ├── AuthorResolver.java # CRUD операції для авторів
│   └── BookResolver.java   # CRUD операції для книг
├── protocol/              # MCP протокол моделі
│   ├── McpMessage.java     # Базове повідомлення MCP
│   ├── Tool.java           # Опис MCP інструменту
│   └── ...                 # Інші MCP структури
├── service/               # Бізнес логіка
│   ├── GraphQLService.java # Сервіс для виконання GraphQL
│   └── McpToolService.java # Реалізація MCP інструментів
├── controller/            # REST контролери
│   └── McpController.java  # MCP HTTP endpoints
├── client/                # MCP клієнт
│   ├── McpClient.java      # Java клієнт для MCP
│   └── McpClientDemo.java  # Демонстрація роботи
└── config/                # Конфігурація Spring
    └── ...
```

## Доступні MCP Tools

### Author Management
- `create_author` - створення нового автора
- `get_authors` - отримання списку авторів
- `get_author` - отримання автора за ID
- `update_author` - оновлення автора
- `delete_author` - видалення автора

### Book Management
- `create_book` - створення нової книги
- `get_books` - отримання списку книг
- `get_book` - отримання книги за ID
- `update_book` - оновлення книги
- `delete_book` - видалення книги

### Relationship Management
- `add_author_to_book` - додавання автора до книги
- `remove_author_from_book` - видалення автора з книги

## Запуск проекту

### Вимоги
- Java 21+
- Maven 3.6+

### Інструкції

1. **Клонування та збірка**:
```bash
git clone <repository-url>
cd mcp-graphql-server
mvn clean install
```

2. **Запуск сервера**:
```bash
mvn spring-boot:run
```

3. **Доступні endpoints**:
- MCP Server: `http://localhost:8080/mcp`
- GraphQL: `http://localhost:8080/graphql`
- GraphiQL: `http://localhost:8080/graphiql`

## Використання

### MCP Client Demo

При запуску додатка автоматично стартує інтерактивна демонстрація MCP клієнта:

```
=== MCP GraphQL Client Demo ===
Initializing MCP client...
✓ Client initialized successfully

=== Available Tools ===
• create_author: Create a new author
• get_authors: Get all authors or search by name
• get_book: Get book by ID
...

=== MCP Client Interactive Demo ===
1. Create Author
2. List Authors
3. Get Author by ID
...
Choose an option:
```

### Приклади GraphQL запитів

**Створення автора**:
```graphql
mutation {
  createAuthor(name: "Stephen King", email: "stephen@example.com", biography: "American author of horror fiction") {
    id
    name
    email
    createdAt
  }
}
```

**Отримання книг з авторами**:
```graphql
query {
  books {
    id
    title
    publicationYear
    authors {
      id
      name
    }
  }
}
```

**Додавання автора до книги**:
```graphql
mutation {
  addAuthorToBook(bookId: 1, authorId: 2) {
    id
    title
    authors {
      name
    }
  }
}
```

### MCP клієнт програматично

```java
McpClient client = new McpClient("http://localhost:8080");

// Ініціалізація
client.initialize().block();

// Отримання списку інструментів
List<Tool> tools = client.listTools().block();

// Створення автора
Object result = client.createAuthor("Isaac Asimov", "isaac@example.com", "Science fiction author").block();

// Створення книги
Object book = client.createBook("Foundation", "978-0-553-29335-0", "Psychohistory novel", 1951).block();
```

## Розуміння концепцій

### Що таке MCP?

Model Context Protocol (MCP) - це протокол, що дозволяє AI системам безпечно підключатися до зовнішніх джерел даних та інструментів. Основні компоненти:

- **Server** - надає доступ до ресурсів і інструментів
- **Client** - споживає ресурси через стандартизований інтерфейс
- **Transport** - механізм комунікації (stdio, SSE, HTTP)

### Різниця між транспортами

| Транспорт | Опис | Використання |
|-----------|------|--------------|
| **stdio** | Комунікація через stdin/stdout | Локальні процеси, CLI інструменти |
| **SSE** | Server-Sent Events | Односпрямовані уведомлення від сервера |
| **Streamable-HTTP** | HTTP з підтримкою потоків | Веб-додатки, REST API |

### GraphQL vs REST

**GraphQL переваги**:
- Один endpoint для всіх операцій
- Клієнт контролює структуру відповіді
- Сильна типізація з автогенерацією документації
- Ефективне завантаження зв'язаних даних

**Наш приклад демонструє**:
- Fold операції (запит декількох пов'язаних сутностей в одному запиті)
- Мутації з поверненням оновлених даних
- Many-to-Many відношення між Author та Book

## Розширення проекту

Проект можна розширити:

1. **Додатковими сутностями** - жанри, видавництва, рецензії
2. **Аутентифікацією** - JWT токени, OAuth2
3. **Валідацією** - детальна валідація вхідних даних
4. **Кешуванням** - Redis для кешування GraphQL запитів
5. **Тестуванням** - Unit та Integration тести
6. **Метриками** - Micrometer для моніторингу

## Висновки

Цей проект демонструє:

- ✅ Повне розуміння MCP протоколу та його призначення
- ✅ Реалізацію Streamable-HTTP транспорту
- ✅ Інтеграцію GraphQL для гнучкого управління даними
- ✅ CRUD операції з Many-to-Many відношеннями
- ✅ Практичне використання Spring Boot та JPA
- ✅ Клієнт-серверну архітектуру з демонстрацією

Проект показує, як сучасні технології можуть ефективно поєднуватися для створення гнучких та масштабованих рішень.