## Setup

1.) Start Elasticsearch `7.8.0+`  
2.) Create the "ledger" index from the documentation as https://github.com/elastic/elasticsearch/blob/v7.9.0/docs/build.gradle#L249-L260

```json
    PUT /ledger
    {
      "settings": {
        "number_of_shards": 2,
        "number_of_replicas": 1
      },
      "mappings": {
        "properties": {
          "type": {
            "type": "keyword"
          },
          "amount": {
            "type": "double"
          }
        }
      }
    }
```
3.) Index bulk sample data from https://github.com/elastic/elasticsearch/blob/v7.9.0/docs/build.gradle#L266-L275

```json
    PUT /ledger/_bulk?refresh
    {"index":{}}
    {"date":"2015/01/01 00:00:00","amount":200,"type":"sale","description":"something"}
    {"index":{}}
    {"date":"2015/01/01 00:00:00","amount":10,"type":"expense","description":"another thing"}
    {"index":{}}
    {"date":"2015/01/01 00:00:00","amount":150,"type":"sale","description":"blah"}
    {"index":{}}
    {"date":"2015/01/01 00:00:00","amount":50,"type":"expense","description":"cost of blah"}
    {"index":{}}
    {"date":"2015/01/01 00:00:00","amount":50,"type":"expense","description":"advertisement"}
```
4.) Run the [Scipted Metric Aggregation example](https://www.elastic.co/guide/en/elasticsearch/reference/7.9/search-aggregations-metrics-scripted-metric-aggregation.html) through a Transport Client (version `7.3.0` in this example)

## Run

Run with `gradlew run`

Supported JVM arguments (pass in as `-D`):
* `cluster.name`
* `index.name`
* `network.host`
* `transport.port`

For example, you can use a different cluster name and index name as

`gradle run -Dcluster.name=MyElasticsearchCluster -Dindex.name=index`.