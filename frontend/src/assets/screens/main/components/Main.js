import { Cell, Column } from 'fixed-data-table';
import React from 'react';
import Relay from 'react-relay';
import { withRouter } from 'react-router';

import CellWithTooltip from '../../../shared/components/CellWithTooltip';
import Table from '../../../shared/components/Table';
import CompareStores from './CompareStores';

function getComparisons({ viewer }) {
  return viewer.comparisons.edges.map(({ node }) => ({
    ...node,
    storeName: node.oldStoreName === node.newStoreName ?
      node.oldStoreName :
      `${node.newStoreName} (${node.oldStoreName})`,
    date: new Date(node.date),
  }));
}

class Main extends React.Component {
  static propTypes = {
    router: React.PropTypes.object.isRequired,
    viewer: React.PropTypes.shape({
      comparisons: React.PropTypes.shape({
        edges: React.PropTypes.arrayOf(React.PropTypes.shape({
          node: React.PropTypes.shape({
            localId: React.PropTypes.number.isRequired,
            oldStoreDir: React.PropTypes.string.isRequired,
            oldStoreName: React.PropTypes.string.isRequired,
            newStoreDir: React.PropTypes.string.isRequired,
            newStoreName: React.PropTypes.string.isRequired,
            date: React.PropTypes.number.isRequired,
          }).isRequired,
        }).isRequired).isRequired,
      }).isRequired,
    }).isRequired,
  };
  _comparisons = getComparisons(this.props);

  componentWillReceiveProps(nextProps) {
    this._comparisons = getComparisons(nextProps);
  }

  _handleRowClick = (_, rowIndex) => {
    this.props.router.push(`/comparison-${this._comparisons[rowIndex].localId}`);
  };

  render() {
    const { viewer } = this.props;

    const comparisons = this._comparisons;

    return (
      <div>
        <CompareStores viewer={viewer} />

        <h3>Comparisons</h3>
        <Table onRowClick={this._handleRowClick} rowsCount={comparisons.length}>
          <Column
            header={<Cell>Old store path</Cell>}
            cell={({ rowIndex }) => <CellWithTooltip>{comparisons[rowIndex].oldStoreDir}</CellWithTooltip>}
            width={500}
          />
          <Column
            header={<Cell>New store path</Cell>}
            cell={({ rowIndex }) => <CellWithTooltip>{comparisons[rowIndex].newStoreDir}</CellWithTooltip>}
            width={500}
          />
          <Column
            header={<Cell>Store name</Cell>}
            cell={({ rowIndex }) => <CellWithTooltip>{comparisons[rowIndex].storeName}</CellWithTooltip>}
            width={200}
          />
          <Column
            header={<Cell>Date</Cell>}
            cell={({ rowIndex }) => <Cell>{comparisons[rowIndex].date.toLocaleString()}</Cell>}
            width={200}
          />
        </Table>
      </div>
    );
  }
}

export default Relay.createContainer(withRouter(Main), {
  fragments: {
    viewer: () => Relay.QL`
      fragment on Viewer {
        ${CompareStores.getFragment('viewer')}
        comparisons(first: 20) {
          edges {
            node {
              localId
              oldStoreDir
              oldStoreName
              newStoreDir
              newStoreName
              date
            }
          }
        }
      }
    `,
  },
});
