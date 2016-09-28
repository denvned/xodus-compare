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
        comparison
        viewer { comparisons }
      }
    `;
  }

  getConfigs() {
    return [
      {
        type: 'FIELDS_CHANGE',
        fieldIDs: { viewer: this.props.viewer.id },
      },
      {
        type: 'REQUIRED_CHILDREN',
        children: [
          Relay.QL`
            fragment on CompareStoresPayload {
              comparison {
                localId
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
