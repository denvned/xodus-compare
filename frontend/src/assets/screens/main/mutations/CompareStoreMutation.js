import Relay from 'react-relay';

export default class AddProjectMutation extends Relay.Mutation {
  static fragments = {
    viewer: () => Relay.QL`
      fragment on Viewer {
        id
      }
    `,
  };

  getMutation() {
    return Relay.QL`mutation { compareStores }`;
  }

  getFatQuery() {
    return Relay.QL`
      fragment on CompareStoresPayload @relay(pattern: true) {
        newComparisonEdge
        viewer { comparisons }
      }
    `;
  }

  getConfigs() {
    return [
      {
        type: 'RANGE_ADD',
        parentName: 'viewer',
        parentID: this.props.viewer.id,
        connectionName: 'comparisons',
        edgeName: 'newComparisonEdge',
        rangeBehaviors: {
          '': 'prepend',
        },
      },
      {
        type: 'REQUIRED_CHILDREN',
        children: [
          Relay.QL`
            fragment on CompareStoresPayload {
              newComparisonEdge {
                node {
                  id
                }
              }
            }
          `,
        ],
      },
    ];
  }

  getVariables() {
    return {
      oldStoreDir: this.props.oldStoreLocation.dir,
      oldStoreName: this.props.oldStoreLocation.storeName,
      newStoreDir: this.props.newStoreLocation.dir,
      newStoreName: this.props.newStoreLocation.storeName,
    };
  }
}
