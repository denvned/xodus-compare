import React from 'react';
import Relay from 'react-relay';
import { withRouter } from 'react-router';
import { Card, CardTitle, CardText } from 'react-toolbox/lib/card';

import propertyTableStyles from '../../styles/propertyTable.scss';

class ComparisonInfo extends React.Component {
  static propTypes = {
    comparison: React.PropTypes.shape({
      localId: React.PropTypes.number.isRequired,
      oldStoreDir: React.PropTypes.string.isRequired,
      oldStoreName: React.PropTypes.string.isRequired,
      newStoreDir: React.PropTypes.string.isRequired,
      newStoreName: React.PropTypes.string.isRequired,
      date: React.PropTypes.number.isRequired,
    }).isRequired,
    router: React.PropTypes.object.isRequired,
  };

  render() {
    const { comparison, router } = this.props;

    const storeName = comparison.oldStoreName === comparison.newStoreName ?
      comparison.oldStoreName :
      `${comparison.newStoreName} (${comparison.oldStoreName})`;

    return (
      <Card onClick={() => { router.push(`/comparison-${comparison.localId}`); }}>
        <CardTitle title="Comparison" />
        <CardText>
          <table className={propertyTableStyles.propertyTable}>
            <tbody>
              <tr><th>Old store path:</th><td>{comparison.oldStoreDir}</td></tr>
              <tr><th>New store path:</th><td>{comparison.newStoreDir}</td></tr>
              <tr><th>Store name:</th><td>{storeName}</td></tr>
              <tr><th>Date:</th><td>{new Date(comparison.date).toLocaleString()}</td></tr>
            </tbody>
          </table>
        </CardText>
      </Card>
    );
  }
}

export default Relay.createContainer(withRouter(ComparisonInfo), {
  fragments: {
    comparison: () => Relay.QL`
      fragment on Comparison {
        localId
        oldStoreDir
        oldStoreName
        newStoreDir
        newStoreName
        date
      }
    `,
  },
});
