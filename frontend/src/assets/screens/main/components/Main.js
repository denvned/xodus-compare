import { Cell, Column, ColumnGroup } from 'fixed-data-table';
import React from 'react';
import Relay from 'react-relay';
import { withRouter } from 'react-router';

import CellWithTooltip from '../../../shared/components/CellWithTooltip';
import Table from '../../../shared/components/Table';
import CompareStores from './CompareStores';
import ProgressCell from './ProgressCell';

function getComparisons({ viewer }) {
  return viewer.comparisons.edges.map(({ node }) => node).map(({
    oldStoreName,
    newStoreName,
    date,
    oldEntityCount,
    newEntityCount,
    oldEntitiesProcessed,
    newEntitiesProcessed,
    ...rest,
  }) => ({
    ...rest,
    storeName: oldStoreName === newStoreName ? oldStoreName : `${newStoreName} (${oldStoreName})`,
    date: new Date(date),
    progress: Math.floor(100 * (oldEntitiesProcessed + newEntitiesProcessed) / (oldEntityCount + newEntityCount))
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
            oldEntityCount: React.PropTypes.number.isRequired,
            newEntityCount: React.PropTypes.number.isRequired,
            oldEntitiesProcessed: React.PropTypes.number.isRequired,
            newEntitiesProcessed: React.PropTypes.number.isRequired,
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

        {!!comparisons.length && <div>
          <h3>Comparisons</h3>
          <Table columnGroups onRowClick={this._handleRowClick} rowsCount={comparisons.length}>
            <ColumnGroup header={<Cell>Store</Cell>}>
              <Column
                header={<Cell>Old path</Cell>}
                cell={({ rowIndex }) => <CellWithTooltip>{comparisons[rowIndex].oldStoreDir}</CellWithTooltip>}
                width={300}
              />
              <Column
                header={<Cell>New path</Cell>}
                cell={({ rowIndex }) => <CellWithTooltip>{comparisons[rowIndex].newStoreDir}</CellWithTooltip>}
                width={300}
              />
              <Column
                header={<Cell>Name</Cell>}
                cell={({ rowIndex }) => <CellWithTooltip>{comparisons[rowIndex].storeName}</CellWithTooltip>}
                width={200}
              />
            </ColumnGroup>
            <ColumnGroup header={<Cell>Date</Cell>}>
              <Column
                cell={({ rowIndex }) => <Cell>{comparisons[rowIndex].date.toLocaleString()}</Cell>}
                width={200}
              />
            </ColumnGroup>
            <ColumnGroup header={<Cell>Progress</Cell>}>
              <Column
                cell={({ rowIndex }) => <ProgressCell progress={comparisons[rowIndex].progress} />}
                width={100}
              />
            </ColumnGroup>
          </Table>
        </div>}
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
              oldEntityCount
              newEntityCount
              oldEntitiesProcessed
              newEntitiesProcessed
            }
          }
        }
      }
    `,
  },
});
