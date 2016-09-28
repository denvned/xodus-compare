import React from 'react';
import Relay from 'react-relay';
import { withRouter } from 'react-router';
import { Card, CardTitle, CardText } from 'react-toolbox/lib/card';

import getEntityUrl from '../../shared/getEntityUrl';
import propertyTableStyles from '../../styles/propertyTable.scss';

class EntityInfo extends React.Component {
  static propTypes = {
    entity: React.PropTypes.shape({
      __typename: React.PropTypes.string.isRequired,
      localId: React.PropTypes.number.isRequired,
      entityId: React.PropTypes.number.isRequired,
    }).isRequired,
    router: React.PropTypes.object.isRequired,
  };

  render() {
    const { entity, router } = this.props;
    const { localId, entityId } = entity;

    return (
      <Card onClick={() => { router.push(getEntityUrl(entity)); }}>
        <CardTitle title="Entity" />
        <CardText>
          <table className={propertyTableStyles.propertyTable}>
            <tbody>
              <tr><th>Id:</th><td>{entityId}</td></tr>
            </tbody>
          </table>
        </CardText>
      </Card>
    );
  }
}

export default Relay.createContainer(withRouter(EntityInfo), {
  fragments: {
    entity: () => Relay.QL`
      fragment on Entity {
        __typename
        localId
        entityId
      }
    `,
  },
});
