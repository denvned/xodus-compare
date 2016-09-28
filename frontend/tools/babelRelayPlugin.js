const babelRelayPlugin = require('babel-relay-plugin');
const fs = require('fs');
const graphql = require('graphql');
const path = require('path');

const schema = require('../build/schema.json').data;

fs.writeFileSync(
  path.join(__dirname, '..', 'build', 'schema.graphql'),
  graphql.printSchema(graphql.buildClientSchema(schema))
);

module.exports = babelRelayPlugin(schema);
