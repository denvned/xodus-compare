import 'fixed-data-table/dist/fixed-data-table.min.css';
import React from 'react';
import ReactDOM from 'react-dom';
import Relay from 'react-relay';
import { applyRouterMiddleware, browserHistory, Route, Router } from 'react-router';
import useRelay from 'react-router-relay';
import 'react-toolbox/lib/commons.scss';

import main from './screens/main';
import comparison from './screens/comparison';
import entityType from './screens/entityType';
import { addedEntity, changedEntity, deletedEntity } from './screens/entity';
import App from './components/App';

Relay.Store.injectNetworkLayer(new Relay.DefaultNetworkLayer('/graphql/'));

ReactDOM.render(
  <Router
    environment={Relay.Store}
    history={browserHistory}
    render={applyRouterMiddleware(useRelay)}
  >
    <Route path="/" component={App}>
      {main}
      {comparison}
      {entityType}
      {addedEntity}
      {changedEntity}
      {deletedEntity}
    </Route>
  </Router>,
  document.getElementById('root')
);
