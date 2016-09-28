import React from 'react';
import { Route } from 'react-router';

import queries from '../../shared/queries';
import { AddedEntity, ChangedEntity, DeletedEntity } from './components/EntityAdapter';

export const addedEntity = <Route path="added-entity-:localId" component={AddedEntity} queries={queries} />;
export const changedEntity = <Route path="changed-entity-:localId" component={ChangedEntity} queries={queries} />;
export const deletedEntity = <Route path="deleted-entity-:localId" component={DeletedEntity} queries={queries} />;
