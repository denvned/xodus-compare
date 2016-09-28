import React from 'react';
import Relay from 'react-relay';
import { withRouter } from 'react-router';
import { Card, CardTitle, CardText } from 'react-toolbox/lib/card';

import propertyTableStyles from '../../styles/propertyTable.scss';

class EntityTypeInfo extends React.Component {
  static propTypes = {
    entityType: React.PropTypes.shape({
      localId: React.PropTypes.number.isRequired,
      typeId: React.PropTypes.number.isRequired,
      oldName: React.PropTypes.string,
      newName: React.PropTypes.string,
    }).isRequired,
    router: React.PropTypes.object.isRequired,
  };

  render() {
    const { entityType, router } = this.props;
    const { localId, typeId, oldName, newName } = entityType;

    const typeName = oldName === newName || !oldName || !newName ? oldName || newName : `${newName} (${oldName})`;

    return (
      <Card onClick={() => { router.push(`/entity-type-${localId}`); }}>
        <CardTitle title="Entity Type" />
        <CardText>
          <table className={propertyTableStyles.propertyTable}>
            <tbody>
              <tr><th>Id:</th><td>{typeId}</td></tr>
              <tr><th>Name:</th><td>{typeName}</td></tr>
            </tbody>
          </table>
        </CardText>
      </Card>
    );
  }
}

export default Relay.createContainer(withRouter(EntityTypeInfo), {
  fragments: {
    entityType: () => Relay.QL`
      fragment on EntityType {
        localId
        typeId
        oldName
        newName
      }
    `,
  },
});
