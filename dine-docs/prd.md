# AI Restaurant Phone Ordering Platform - PRD

## 1. Product Overview

An AI-powered restaurant platform where restaurants can onboard and configure their business information. The platform provides an AI phone agent (via Twilio + TTS/STT) that handles customer calls for dine-in reservations and takeout orders on behalf of the restaurants.

## 2. System Architecture

```
┌────────┐    WebSocket     ┌──────────────┐    HTTP/REST   ┌─────────────┐
│ Twilio │ ◄──────────────► │ Python Agent │ ◄────────────► │ Java Backend│
│  Phone │   Real-time      │ + STT + TTS  │   Business     │  (Spring    │
│        │   Audio Stream   │ + LLM        │   API          │   Boot)     │
└────────┘                  └──────────────┘                └─────────────┘
                                                                  │
                                                            ┌─────────────┐
                                                            │  Vue Frontend│
                                                            │  (Admin UI)  │
                                                            └──────────────┘
```

- **Java Backend (dine-backend)**: Clean, independent business logic backend. Exposes REST APIs.
- **Python AI Agent (dine-agent)**: Handles voice processing + AI conversation + calls Java APIs via HTTP.
- **Vue Frontend (dine-frontend)**: Restaurant admin panel for configuration and order management.

## 3. Feature Modules

### 3.1 Restaurant Basic Info

Restaurant profile information displayed to customers and used by the AI agent.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Restaurant name |
| description | String | No | Restaurant description |
| address | String | Yes | Full address |
| phone | String | Yes | Contact phone number |
| timezone | String | Yes | Restaurant timezone (e.g. America/New_York) |
| logo | String | No | Logo image URL |
| images | List\<String\> | No | Restaurant photo URLs |
| status | Enum | Yes | OPEN / PAUSED / CLOSED |
| created_at | DateTime | Auto | Record creation time |
| updated_at | DateTime | Auto | Record update time |

### 3.2 Operating Hours

Supports complex scheduling: different hours per day, multiple time slots per day, different hours per order type, and special date overrides.

#### 3.2.1 Regular Hours

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| day_of_week | Enum | Yes | MON / TUE / WED / THU / FRI / SAT / SUN |
| order_type | Enum | No | DINE_IN / TAKEOUT / null (null = applies to all) |
| open_time | Time | Yes | Opening time for this slot |
| close_time | Time | Yes | Closing time for this slot |

One day can have multiple time slots. Example:
- MON: 11:00-14:00 (lunch), 17:00-22:00 (dinner)
- MON TAKEOUT: 11:00-14:00, 17:00-21:30 (closes earlier)

#### 3.2.2 Special Dates

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| date | Date | Yes | Specific date |
| is_closed | Boolean | Yes | true = closed all day |
| open_time | Time | Conditional | Required if is_closed = false |
| close_time | Time | Conditional | Required if is_closed = false |
| note | String | No | Reason (e.g. "Christmas Day") |

#### 3.2.3 Last Order

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| last_order_minutes_before_close | Integer | Yes | Stop accepting orders X minutes before closing |

### 3.3 Menu Management

#### 3.3.1 Category

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Category name (e.g. Appetizers, Main Course, Drinks) |
| description | String | No | Category description |
| sort_order | Integer | Yes | Display order |

#### 3.3.2 Menu Item

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Item name |
| description | String | No | Item description |
| price | Decimal | Yes | Base price |
| image | String | No | Item image URL |
| category_id | Long | Yes | Associated category |
| allergens | List\<String\> | No | Allergen tags (e.g. nuts, dairy, gluten) |
| tags | List\<String\> | No | Labels (e.g. vegetarian, spicy, signature, new) |
| available | Boolean | Yes | Whether the item is currently available |
| sort_order | Integer | Yes | Display order within category |

**Variants** (size/spec options with different prices):

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Variant name (e.g. Small, Medium, Large) |
| price | Decimal | Yes | Price for this variant |
| sort_order | Integer | Yes | Display order |

When an item has variants, the item's base `price` is ignored; each variant has its own price.

**Modifier Groups** (customization options):

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Group name (e.g. "Spice Level", "Add-ons") |
| selection_type | Enum | Yes | SINGLE / MULTI |
| required | Boolean | Yes | Whether customer must choose |
| max_selections | Integer | No | Max choices for MULTI type (0 = unlimited) |

**Modifier Options** (choices within a group):

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Option name (e.g. "Extra Cheese", "Mild") |
| extra_price | Decimal | Yes | Additional cost (0 = no extra charge) |
| is_default | Boolean | Yes | Whether selected by default |

**Availability Schedule** (item-level supply time control):

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| available_days | List\<Enum\> | No | Days of week (null = every day) |
| available_start_time | Time | No | Start time (null = from opening) |
| available_end_time | Time | No | End time (null = until closing) |

#### 3.3.3 Combo (Set Meal)

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Combo name |
| description | String | No | Combo description |
| price | Decimal | Yes | Combo price |
| image | String | No | Combo image URL |
| available | Boolean | Yes | Whether currently available |

**Combo Groups** (what the customer picks from):

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Group name (e.g. "Choose an Entree", "Choose a Drink") |
| pick_count | Integer | Yes | How many items to pick from this group |
| required | Boolean | Yes | Whether this group must be selected |
| selectable_items | List\<Long\> | Yes | Item IDs available for selection |

Combo inherits the same availability schedule structure as menu items.

### 3.4 Table Management

#### 3.4.1 Section (Dining Area)

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Section name (e.g. Main Hall, Patio, VIP Room, Bar) |
| smoking | Boolean | Yes | Whether smoking is allowed |
| surcharge | Decimal | No | Extra fee for this section (e.g. VIP room fee) |
| note | String | No | Additional info |

#### 3.4.2 Table

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| table_number | String | Yes | Table identifier |
| capacity | Integer | Yes | Number of seats |
| section_id | Long | Yes | Which section this table belongs to |
| mergeable | Boolean | Yes | Whether this table can be merged with adjacent tables |
| status | Enum | Yes | AVAILABLE / MAINTENANCE |

#### 3.4.3 Queue Settings

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| queue_enabled | Boolean | Yes | Whether walk-in queue is supported |
| estimated_wait_strategy | String | No | How to estimate wait time |

### 3.5 Parking Info

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| has_parking | Boolean | Yes | Whether parking is available |
| parking_type | Enum | No | OWN / PARTNER / STREET |
| capacity | Integer | No | Number of spots |
| fee_type | Enum | No | FREE / FREE_WITH_MIN_SPEND / HOURLY |
| free_with_min_spend_amount | Decimal | No | Minimum spend for free parking |
| hourly_rate | Decimal | No | Hourly parking rate |
| address | String | No | Parking location (if different from restaurant) |
| notes | String | No | Additional instructions (e.g. "Enter from back door") |

### 3.6 Tax Settings

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| tax_rate | Decimal | Yes | Tax rate as percentage (e.g. 8.875) |
| tax_name | String | Yes | Tax label (e.g. "Sales Tax") |
| tax_inclusive | Boolean | Yes | Whether menu prices include tax (US typically false) |

### 3.7 Order Settings

#### 3.7.1 General

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| accepted_order_types | List\<Enum\> | Yes | DINE_IN / TAKEOUT (DELIVERY reserved, not open) |

#### 3.7.2 Dine-in Settings

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| reservation_enabled | Boolean | Yes | Whether reservations are accepted |
| max_advance_days | Integer | Yes | Maximum days in advance to reserve |
| min_advance_minutes | Integer | Yes | Minimum minutes in advance to reserve |
| max_reservations_per_slot | Integer | Yes | Max reservations per time slot |
| section_preference_enabled | Boolean | Yes | Whether customers can request a preferred section |
| pre_order_enabled | Boolean | Yes | Whether customers can order food when reserving |
| pre_order_required | Boolean | Yes | Whether customers must order food when reserving |

#### 3.7.3 Takeout Settings

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| min_prep_minutes | Integer | Yes | Minimum preparation time |
| scheduled_pickup_enabled | Boolean | Yes | Whether customers can schedule a pickup time |
| max_scheduled_advance_hours | Integer | No | Max hours in advance for scheduled pickup |
| max_orders_per_slot | Integer | Yes | Max takeout orders per time slot |
| pickup_instructions | String | No | Pickup location instructions (e.g. "Side door pickup") |

#### 3.7.4 Cancellation Policy

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| allow_cancellation | Boolean | Yes | Whether order cancellation is allowed |
| cancel_deadline_hours | Integer | No | Must cancel at least X hours before (0 = anytime) |
| cancel_reason_required | Boolean | Yes | Whether cancellation reason is mandatory |
| cancel_policy_note | String | No | Policy text AI reads to customers |

Example: "Cancellations must be made at least 2 hours before your reservation time."

#### 3.7.5 Capacity Control

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| peak_hour_order_limit | Integer | No | Max orders per slot during peak hours |

### 3.8 AI Phone Settings

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| language | String | Yes | Currently only "en" (English) |
| greeting_message | String | No | Custom greeting (e.g. "Thank you for calling Mario's!") |

#### 3.8.1 Active Hours

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| day_of_week | Enum | Yes | MON - SUN |
| start_time | Time | Yes | Start accepting calls |
| end_time | Time | Yes | Stop accepting calls |

#### 3.8.2 FAQ

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| question | String | Yes | Common question |
| answer | String | Yes | Answer for AI to reference |
| sort_order | Integer | Yes | Priority order |

#### 3.8.3 Special Instructions

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| instruction | String | Yes | Guidance for AI behavior |
| sort_order | Integer | Yes | Priority order |

Examples:
- "Always recommend our signature dish: Truffle Pasta"
- "When asked about gluten-free options, mention the Grilled Salmon and Caesar Salad"
- "We do not allow outside food or drinks"

#### 3.8.4 Escalation (Transfer to Human)

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| escalation_phone | String | Yes | Phone number to transfer to |
| escalation_triggers | List\<Enum\> | Yes | CUSTOMER_REQUEST / AI_UNABLE / COMPLAINT |

### 3.9 Order

The core output of the AI phone agent.

#### 3.9.1 Order

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| order_number | String | Auto | Unique order identifier |
| restaurant_id | Long | Yes | Associated restaurant |
| type | Enum | Yes | DINE_IN / TAKEOUT |
| status | Enum | Yes | PENDING / CONFIRMED / COMPLETED / CANCELLED |
| customer_name | String | Yes | Customer name |
| customer_phone | String | Yes | Customer phone (from caller ID) |
| customer_notes | String | No | Special requests |
| call_id | String | No | Twilio call SID for reference |
| cancel_reason | String | No | Reason if cancelled |
| created_at | DateTime | Auto | Order creation time |
| updated_at | DateTime | Auto | Last update time |

**Order Status Flow:**

```
PENDING ──► CONFIRMED ──► COMPLETED
                │
                └──────────► CANCELLED
```

#### 3.9.2 Dine-in Specific Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| reservation_date | Date | Yes | Date of reservation |
| reservation_time | Time | Yes | Time of reservation |
| party_size | Integer | Yes | Number of guests |
| section_preference | String | No | Preferred dining area |

#### 3.9.3 Takeout Specific Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| pickup_time | DateTime | Yes | Expected pickup time |

#### 3.9.4 Order Item

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| item_id | Long | Conditional | Menu item ID (if ordering a regular item) |
| combo_id | Long | Conditional | Combo ID (if ordering a combo) |
| quantity | Integer | Yes | Number of this item |
| variant_name | String | No | Selected variant (e.g. "Large") |
| selected_modifiers | List | No | Chosen modifiers with names and prices |
| item_note | String | No | Item-level note (e.g. "no onions") |
| subtotal | Decimal | Yes | Line item subtotal |

#### 3.9.5 Order Totals

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| subtotal | Decimal | Yes | Sum of all order items |
| tax | Decimal | Yes | Calculated tax amount |
| total | Decimal | Yes | Final total (subtotal + tax) |

### 3.10 Account & Permissions

#### 3.10.1 Roles

| Role | Permissions |
|------|------------|
| SUPER_ADMIN | Full CRUD on all restaurant settings + add/remove/edit ADMIN users |
| ADMIN | Full CRUD on all restaurant settings (cannot manage accounts) |

#### 3.10.2 Account Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| username | String | Yes | Login username |
| password | String | Yes | Hashed password |
| role | Enum | Yes | SUPER_ADMIN / ADMIN |
| restaurant_id | Long | Yes | Associated restaurant |
| created_at | DateTime | Auto | Account creation time |
| updated_at | DateTime | Auto | Last update time |

## 4. Non-functional Notes

- **Language**: AI phone agent supports English only for now.
- **Payment**: Platform only records orders; no payment processing at this stage.
- **Delivery**: Data model reserves DELIVERY as an order type but it is not open.
- **Tips**: Not supported at this stage.
- **Multi-store**: Each store is treated as an independent restaurant entity.
