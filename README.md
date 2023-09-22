# Ahrefs input plugin for Embulk

embulk-input-ahrefs is the gem preparing Embulk input plugins for (Ahrefs)[https://ahrefs.jp/].

- Domain rating
- Backlinks stats
- Refdomains history

This plugin uses Ahrefs REST API.

## Overview

Required Embulk version >= 0.11.0.

- Plugin type: input
- Resume supported: no
- Cleanup supported: no
- Guess supported: no

## Install

embulk.properties

```
plugins.input.ahrefs=maven:io.github.guchey.embulk.input.ahrefs:ahrefs:0.1.0-ALPHA
```

## Configuration

### API

Below parameters are shown in (https://docs.ahrefs.com/)[https://docs.ahrefs.com/]

### Base configuration parametor

All target have this configuration parameters.

|name|required|default value|description|
|----|--------|-------------|-----------|
|api_key|true||Ahrefs API key|

### Domain rating

|name|required|default value|description|
|----|--------|-------------|-----------|
|protocol|false|both|The protocol of your target.　Allowed values:　both / http / https|
|date|true||A date to report metrics on in YYYY-MM-DD format.|
|target|true||The target of the search: a domain or a URL.|

### Backlinks stats

|name|required|default value|description|
|----|--------|-------------|-----------|
|mode|false|subdomains|The scope of the search based on the target you entered.Allowed values:　exact / prefix / domain / subdomains|
|protocol|false|both|The protocol of your target.　Allowed values:　both / http / https|
|date|true||A date to report metrics on in YYYY-MM-DD format.|
|target|true||The target of the search: a domain or a URL.|

### Refdomains history

|name|required|default value|description|
|----|--------|-------------|-----------|
|date_to|false||The end date of the historical period in YYYY-MM-DD format.|
|history_grouping|false|monthly|The time interval used to group historical data.Allowed values:　daily / weekly / monthly|
|mode|false|subdomains|The scope of the search based on the target you entered.Allowed values:　exact / prefix / domain / subdomains|
|protocol|false|both|The protocol of your target.　Allowed values:　both / http / https|
|date_from|true||The start date of the historical period in YYYY-MM-DD format.|
|target|true||The target of the search: a domain or a URL.|

## Example

```yml
in:
  type: ahrefs
  api_key: API_KEY
  resource: domain_rating
  target: example.com
  date_to: "2023-02-01"
  date_from: "2023-01-01"
out: {type: stdout}
```
