import React from 'react';
import { IndexRoute } from 'react-router';

import queries from '../../shared/queries';
import Main from './components/Main';

export default <IndexRoute component={Main} queries={queries} />;
