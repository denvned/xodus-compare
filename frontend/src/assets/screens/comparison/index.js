import React from 'react';
import { Route } from 'react-router';

import queries from '../../shared/queries';
import Comparison from './components/Comparison';

export default <Route path="comparison-:localId" component={Comparison} queries={queries} />;
