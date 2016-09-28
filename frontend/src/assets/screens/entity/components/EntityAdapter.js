import React from 'react';
import Relay from 'react-relay';

import Entity from './Entity';

function EntityAdapter({ viewer }) {
  const { entity } = viewer;

  if (!entity) {
    return <div>Entity is not found</div>;
  }

  return <Entity entity={entity} />;
}

EntityAdapter.propTypes = {
  viewer: React.PropTypes.shape({
    entity: React.PropTypes.object,
  }).isRequired,
};

export const AddedEntity = Relay.createContainer(EntityAdapter, {
  initialVariables: {
    localId: null,
  },
  fragments: {
    viewer: () => Relay.QL`
      fragment on Viewer {
        entity: addedEntity(localId: $localId) {
          ${Entity.getFragment('entity')}
        }
      }
    `,
  },
});

export const ChangedEntity = Relay.createContainer(EntityAdapter, {
  initialVariables: {
    localId: null,
  },
  fragments: {
    viewer: () => Relay.QL`
      fragment on Viewer {
        entity: changedEntity(localId: $localId) {
          ${Entity.getFragment('entity')}
        }
      }
    `,
  },
});

export const DeletedEntity = Relay.createContainer(EntityAdapter, {
  initialVariables: {
    localId: null,
  },
  fragments: {
    viewer: () => Relay.QL`
      fragment on Viewer {
        entity: deletedEntity(localId: $localId) {
          ${Entity.getFragment('entity')}
        }
      }
    `,
  },
});
