    # 🌐 HTTP Status Codes — Complete Reference

HTTP status codes are **three-digit numbers** that tell the client what happened to their request.  
They’re grouped into **five categories** based on the first digit:

| Class | Range | Meaning |
|--------|--------|----------|
| **1xx** | 100–199 | Informational — request received, continuing process |
| **2xx** | 200–299 | Success — the request was successfully received and accepted |
| **3xx** | 300–399 | Redirection — further action needed to complete request |
| **4xx** | 400–499 | Client Error — request has bad syntax or cannot be fulfilled |
| **5xx** | 500–599 | Server Error — server failed to fulfill a valid request |

---

## 🟦 1xx — Informational

| Code | Message | Description |
|------|----------|--------------|
| **100** | Continue | Server received headers; client should continue. |
| **101** | Switching Protocols | Server switching protocols as requested. |
| **102** | Processing (WebDAV) | Request received and is being processed. |
| **103** | Early Hints | Preload resources before final response. |

---

## 🟩 2xx — Success

| Code | Message | Description |
|------|----------|--------------|
| **200** | OK | Request succeeded. |
| **201** | Created | Resource created successfully. |
| **202** | Accepted | Request accepted for processing. |
| **203** | Non-Authoritative Information | Response modified by proxy. |
| **204** | No Content | Request succeeded, no content returned. |
| **205** | Reset Content | Reset document view or form. |
| **206** | Partial Content | Partial data returned (range request). |
| **207** | Multi-Status (WebDAV) | Multiple independent results. |
| **208** | Already Reported (WebDAV) | Element already reported earlier. |
| **226** | IM Used | Instance manipulation applied. |

---

## 🟨 3xx — Redirection

| Code | Message | Description |
|------|----------|--------------|
| **300** | Multiple Choices | Several options available. |
| **301** | Moved Permanently | Resource moved to new URI. |
| **302** | Found | Temporary redirect. |
| **303** | See Other | Redirect to another resource using GET. |
| **304** | Not Modified | Resource unchanged (cache). |
| **305** | Use Proxy *(deprecated)* | Must access via proxy. |
| **307** | Temporary Redirect | Same as 302 but method unchanged. |
| **308** | Permanent Redirect | Same as 301 but method unchanged. |

---

## 🟥 4xx — Client Error

| Code | Message | Description |
|------|----------|--------------|
| **400** | Bad Request | Invalid syntax or parameters. |
| **401** | Unauthorized | Authentication required or failed. |
| **402** | Payment Required *(reserved)* | Rarely used. |
| **403** | Forbidden | Authenticated but no permission. |
| **404** | Not Found | Resource not found. |
| **405** | Method Not Allowed | HTTP method not supported. |
| **406** | Not Acceptable | Response format not acceptable. |
| **407** | Proxy Authentication Required | Authenticate with proxy first. |
| **408** | Request Timeout | Client took too long. |
| **409** | Conflict | Request conflicts with server state. |
| **410** | Gone | Resource permanently removed. |
| **411** | Length Required | Missing `Content-Length` header. |
| **412** | Precondition Failed | Precondition header not met. |
| **413** | Payload Too Large | Request body too big. |
| **414** | URI Too Long | URL too long to process. |
| **415** | Unsupported Media Type | Content type not supported. |
| **416** | Range Not Satisfiable | Invalid byte range. |
| **417** | Expectation Failed | Expectation header can’t be met. |
| **418** | I’m a teapot *(fun RFC 2324)* | Joke code (not used in production). |
| **421** | Misdirected Request | Sent to wrong server. |
| **422** | Unprocessable Entity | Semantic validation error. |
| **423** | Locked | Resource is locked. |
| **424** | Failed Dependency | Dependent request failed. |
| **425** | Too Early | Request sent too early. |
| **426** | Upgrade Required | Must upgrade protocol (e.g., HTTPS). |
| **428** | Precondition Required | Missing precondition header. |
| **429** | Too Many Requests | Rate limit exceeded. |
| **431** | Request Header Fields Too Large | Headers too large. |
| **451** | Unavailable For Legal Reasons | Blocked by law. |

---

## 🟥 5xx — Server Error

| Code | Message | Description |
|------|----------|--------------|
| **500** | Internal Server Error | Generic server error. |
| **501** | Not Implemented | Method not supported. |
| **502** | Bad Gateway | Invalid response from upstream server. |
| **503** | Service Unavailable | Server overloaded or down. |
| **504** | Gateway Timeout | Upstream server didn’t respond. |
| **505** | HTTP Version Not Supported | Unsupported HTTP version. |
| **506** | Variant Also Negotiates | Negotiation config error. |
| **507** | Insufficient Storage (WebDAV) | Not enough storage. |
| **508** | Loop Detected (WebDAV) | Infinite loop detected. |
| **510** | Not Extended | Missing extensions for request. |
| **511** | Network Authentication Required | Network auth required. |

---

## ⚡ Quick REST API Summary

| Code | Meaning | Typical Use |
|------|----------|--------------|
| **200** | OK | GET success |
| **201** | Created | POST success |
| **204** | No Content | DELETE success |
| **400** | Bad Request | Invalid input |
| **401** | Unauthorized | Missing/invalid token |
| **403** | Forbidden | No access rights |
| **404** | Not Found | Resource doesn’t exist |
| **409** | Conflict | Duplicate/invalid state |
| **422** | Unprocessable Entity | Validation error |
| **500** | Internal Server Error | Server issue |
| **503** | Service Unavailable | Maintenance or overload |

---

### 📘 Reference

Defined primarily in:
- [RFC 9110: HTTP Semantics](https://datatracker.ietf.org/doc/html/rfc9110)
- [RFC 7231, 7232, 7233, 7235] (older versions)
