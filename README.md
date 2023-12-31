![gem v0.9](https://github.com/guchey/embulk-input-ahrefs/actions/workflows/e2e-embulk-gem.yml/badge.svg?branch=main)
![gem v0.10](https://github.com/guchey/embulk-input-ahrefs/actions/workflows/e2e-embulk-gem-v_0_10.yml/badge.svg?branch=main)
![maven v0.10](https://github.com/guchey/embulk-input-ahrefs/actions/workflows/e2e-embulk-maven-v_0_10.yml/badge.svg?branch=main)

# Ahrefs input plugin for Embulk

embulk-input-ahrefs is the gem preparing Embulk input plugins
for [Ahrefs API](https://docs.ahrefs.com/docs/api/reference).

- Domain rating
- Backlinks stats
- Refdomains history

This plugin uses Ahrefs REST API.

## Overview

Required Embulk version >= 0.9.25

- Plugin type: input
- Resume supported: no
- Cleanup supported: no
- Guess supported: no

## Install

Use gem to get the plugin.

```
embulk gem install embulk-input-ahrefs
```

Use maven-dependency-plugin to get the plugin.

```
mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get -Dartifact=io.github.guchey.embulk.input.ahrefs:embulk-input-ahrefs:0.1.1
```

Specify plugin in embulk.properties.

```
plugins.input.ahrefs=maven:io.github.guchey.embulk.input.ahrefs:ahrefs:0.1.1
```



## Configuration

### API

Below parameters are shown in [https://docs.ahrefs.com/](https://docs.ahrefs.com/)

### Base configuration parameter

All target have this configuration parameters.

| name    | required | default value | description    |
|---------|----------|---------------|----------------|
| api_key | true     |               | Ahrefs API key |

### Site Explorer

#### Domain rating

`resource: site_explorer_domain_rating`

| name     | required | default value | description                                       |
|----------|----------|---------------|---------------------------------------------------|
| protocol | false    | both          | The protocol of your target.                      |
| date     | true     |               | A date to report metrics on in YYYY-MM-DD format. |
| target   | true     |               | The target of the search: a domain or a URL.      |

#### Backlinks stats

`resource: site_explorer_backlink_stats`

| name     | required | default value | description                                              |
|----------|----------|---------------|----------------------------------------------------------|
| mode     | false    | subdomains    | The scope of the search based on the target you entered. |
| protocol | false    | both          | The protocol of your target.                             |
| date     | true     |               | A date to report metrics on in YYYY-MM-DD format.        |
| target   | true     |               | The target of the search: a domain or a URL.             |

#### Metrics

`resource: site_explorer_metrics`

| name        | required | default value | description                                                                                            |
|-------------|----------|---------------|--------------------------------------------------------------------------------------------------------|
| country     | false    |               | A two-letter country code.                                                                             |
| mode        | false    | subdomains    | The scope of the search based on the target you entered.                                               |
| protocol    | false    | both          | The protocol of your target.                                                                           |
| volume_mode | false    | monthly       | The search volume calculation mode: monthly or average. It affects volume, traffic, and traffic value. |
| date        | true     |               | A date to report metrics on in YYYY-MM-DD format.                                                      |
| target      | true     |               | The target of the search: a domain or a URL.                                                           |

#### Refdomains history

`resource: site_explorer_ref_domains_history`

| name             | required | default value | description                                                   |
|------------------|----------|---------------|---------------------------------------------------------------|
| date_to          | false    |               | The end date of the historical period in YYYY-MM-DD format.   |
| history_grouping | false    | monthly       | The time interval used to group historical data.              |
| mode             | false    | subdomains    | The scope of the search based on the target you entered.      |
| protocol         | false    | both          | The protocol of your target.　                                 |
| date_from        | true     |               | The start date of the historical period in YYYY-MM-DD format. |
| target           | true     |               | The target of the search: a domain or a URL.                  |

#### Url rating history

`resource: site_explorer_url_rating_history`

| name             | required | default value | description                                                   |
|------------------|----------|---------------|---------------------------------------------------------------|
| date_to          | false    |               | The end date of the historical period in YYYY-MM-DD format.   |
| history_grouping | false    | monthly       | The time interval used to group historical data.              |
| date_from        | true     |               | The start date of the historical period in YYYY-MM-DD format. |
| target           | true     |               | The target of the search: a domain or a URL.                  |

#### Metrics history

`resource: site_explorer_metrics_history`

| name             | required | default value | description                                                                                            |
|------------------|----------|---------------|--------------------------------------------------------------------------------------------------------|
| country          | false    |               | A two-letter country code.                                                                             |
| date_to          | false    |               | The end date of the historical period in YYYY-MM-DD format.                                            |
| history_grouping | false    | monthly       | The time interval used to group historical data.                                                       |
| volume_mode      | false    | monthly       | The search volume calculation mode: monthly or average. It affects volume, traffic, and traffic value. |
| date_from        | true     |               | The start date of the historical period in YYYY-MM-DD format.                                          |
| target           | true     |               | The target of the search: a domain or a URL.                                                           |

#### Pages history

`resource: site_explorer_pages_history`

| name             | required | default value | description                                                   |
|------------------|----------|---------------|---------------------------------------------------------------|
| country          | false    |               | A two-letter country code.                                    |
| date_to          | false    |               | The end date of the historical period in YYYY-MM-DD format.   |
| history_grouping | false    | monthly       | The time interval used to group historical data.              |
| mode             | false    | subdomains    | The scope of the search based on the target you entered.      |
| protocol         | false    | both          | The protocol of your target.　                                 |
| date_from        | true     |               | The start date of the historical period in YYYY-MM-DD format. |
| target           | true     |               | The target of the search: a domain or a URL.                  |

#### Keywords history

`resource: site_explorer_keywords_history`

| name             | required | default value | description                                                   |
|------------------|----------|---------------|---------------------------------------------------------------|
| country          | false    |               | A two-letter country code.                                    |
| date_to          | false    |               | The end date of the historical period in YYYY-MM-DD format.   |
| history_grouping | false    | monthly       | The time interval used to group historical data.              |
| mode             | false    | subdomains    | The scope of the search based on the target you entered.      |
| protocol         | false    | both          | The protocol of your target.　                                 |
| date_from        | true     |               | The start date of the historical period in YYYY-MM-DD format. |
| target           | true     |               | The target of the search: a domain or a URL.                  |

#### Metrics by country

`resource: site_explorer_metrics_by_country`

| name        | required | default value | description                                                                                            |
|-------------|----------|---------------|--------------------------------------------------------------------------------------------------------|
| limit       | false    | 1000          | The number of results to return.                                                                       |
| mode        | false    | subdomains    | The scope of the search based on the target you entered.                                               |
| offset      | false    | 0             | Returned results will start from the row indicated in the offset value.                                |
| protocol    | false    | both          | The protocol of your target.　                                                                          |
| volume_mode | false    | monthly       | The search volume calculation mode: monthly or average. It affects volume, traffic, and traffic value. |
| date        | true     |               | A date to report metrics on in YYYY-MM-DD format.                                                      |
| target      | true     |               | The target of the search: a domain or a URL.                                                           |

#### Pages by traffic

`resource: site_explorer_pages_by_traffic`

| name        | required | default value | description                                                                                            |
|-------------|----------|---------------|--------------------------------------------------------------------------------------------------------|
| country     | false    |               | A two-letter country code.                                                                             |
| mode        | false    | subdomains    | The scope of the search based on the target you entered.                                               |
| protocol    | false    | both          | The protocol of your target.　                                                                          |
| volume_mode | false    | monthly       | The search volume calculation mode: monthly or average. It affects volume, traffic, and traffic value. |
| target      | true     |               | The target of the search: a domain or a URL.                                                           |


### Keywords Explorer

#### Overview

`resource: keywords_explorer_overview`

| name            | required | default value | description                                                                     |
|-----------------|----------|---------------|---------------------------------------------------------------------------------|
| keyword_list_id | false    |               | The id of an existing keyword list to show metrics for.                         |
| keywords        | false    |               | A comma-separated list of keywords to show metrics for.                         |
| limit           | false    | 1000          | The number of results to return.                                                |
| offset          | false    | 0             | Returned results will start from the row indicated in the offset value.         |
| order_by        | false    |               | A column to order results by. See response schema for valid column identifiers. |
| search_engine   | false    | google        | The search engine to get keyword metrics for.                                   |
| timeout         | false    |               | A manual timeout duration in seconds.                                           |
| where           | false    | both          | The filter expression. The following column identifiers are recognized          |
| country         | true     |               | A two-letter country code.                                                      |
| select          | true     |               | select                                                                          |

## Example

```yml
in:
  type: ahrefs
  api_key: API_KEY
  resource: site_explorer_domain_rating
  target: ahrefs.com
  date_to: "2023-02-01"
  date_from: "2023-01-01"
out:
  type: stdout
```
