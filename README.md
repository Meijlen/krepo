# KRepo

**Lightweight, extensible Repository pattern abstraction for Kotlin/Ktor**

[![Kotlin](https://img.shields.io/badge/kotlin-2.0.20-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

> **No Spring. No boilerplate. Just clean, suspendable, dynamic repositories.**

---

## Important Foreword

> **I am practically unfamiliar with open-source culture and advanced version control. I would appreciate help with formatting readme.md, etc.**
---

## Features

- **Suspendable CRUD** — full `suspend` support, coroutine-friendly
- **Zero reflection at runtime** — metadata built once via reflection, then cached
- **Proxy-based repositories** — no manual implementation needed

---

## Upcoming features
- **Dynamic method parsing** — `findByNameAndAgeGreaterThan` → SQL/Mongo query via implementations
- **Pluggable data access** — `DataAccessor` for Exposed, MongoDB, JDBC, in-memory
- **Ktor-ready** — lightweight, no heavy DI
---

## Project To-Do List

| Ready | Module          | Description |
|---|-----------------|-------------|
| ✓ | `krepo-core`    | Core abstraction: `RepositoryContext`, `DataAccessor`, proxies |
| - | `krepo-exposed` | Exposed ORM integration |
| - | `krepo-mongo`   | MongoDB driver support | 
| - | `sample`        | Full Ktor app example |

---

## License

```
Copyright 2025 Meijlen

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

Made with ❤️ in Kotlin