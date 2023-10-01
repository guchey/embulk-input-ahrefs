FROM stoplight/prism:4
CMD ["mock", "-h", "0.0.0.0", "https://guchey.github.io/embulk-input-ahrefs/api.yml"]