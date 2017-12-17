# Android Van Sales Sample Application
---

An app that can sync and fetch item details from a specified network stream, do sales and print the invoice.

---
## Network Sync :
- Sync Pending Invoices
- Fetch new stock status and overwrite the app database
- Fetch related item data
- Fetch Unit wise Item Price

### Byte Stream Info
The following is expected from the Server on connection: (Simple TCP Socket Connection)

- Server Sends `6` (int)
- App verifies and sends back `6`
- Connection established

Requests are made by the app, for each request:
