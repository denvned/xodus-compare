import React from 'react';
import { Route } from 'react-router';

import queries from '../../shared/queries';
import EntityType from './components/EntityType';

export default <Route path="entity-type-:localId" component={EntityType} queries={queries} />;
