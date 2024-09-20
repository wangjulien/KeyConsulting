# KeyConsulting

Key Consulting - Task Service

## RESTful API

### 1. Creating new task

<details>
 <summary><code>POST</code> <code><b>/</b></code> <code>{localhost:8081}/task-service/tasks</code></summary>

#### Request Body

> | name |  type     | data type      | description                                |
> |------|-----------|----------------|--------------------------------------------|
> | body |  required | object (JSON ) | `{"label": String, "description": String}` |

#### Responses

> | http code | content-type                      | response                                                                    |
> |-----------|-----------------------------------|-----------------------------------------------------------------------------|
> | `200`     | `application/json`                | `{"id": long, "label": String, "description": String , "completed": false}` |

</details>

### 2. Patch task status

<details>
 <summary><code>POST</code> <code><b>/</b></code> <code>{localhost:8081}/task-service/tasks</code></summary>

#### Request Body

> | name |  type     | data type      | description                          |
> |------|-----------|----------------|--------------------------------------|
> | body |  required | object (JSON ) | `{"id": long, "completed": boolean}` |

#### Responses

> | http code | content-type                      | response                                                                      |
> |-----------|-----------------------------------|-------------------------------------------------------------------------------|
> | `200`     | `application/json`                | `{"id": long, "label": String, "description": String , "completed": boolean}` |
> | `400`     | `application/json`                | NotFound error with ProblemDetail object for not existing ID                  |
> | `404`     | `application/json`                | BadRequest error with ProblemDetail object for no "id" / "description" offered|

</details>

### 3. Get tasks by status

<details>
 <summary><code>GET</code> <code><b>/</b></code> <code>{localhost:8081}/task-service/tasks</code></summary>

##### Request Param

> | name | type     | data type | description              |
> |------|----------|-----------|--------------------------|
> | isCompleted | optional | Boolean   | 'null' means get all tasks |

##### Responses

> | http code | content-type                      | response             |
> |-----------|-----------------------------------|----------------------|
> | `200`     | `application/json`                | List of task objects |

</details>

### 4. Get task by ID

<details>
 <summary><code>GET</code> <code><b>/</b></code> <code>{localhost:8081}/task-service/tasks/{id}</code></summary>

##### Path Variable

> | name | type     | data type | description              |
> |------|----------|-----------|--------------------------|
> | id   | required | long      |  |

##### Responses

> | http code | content-type                      | response                                                                      |
> |-----------|-----------------------------------|-------------------------------------------------------------------------------|
> | `200`     | `application/json`                | `{"id": long, "label": String, "description": String , "completed": boolean}` |
> | `400`     | `application/json`                | Not found error with ProblemDetail object                                     |

</details>

