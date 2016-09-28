import { Cell, Column, ColumnGroup } from 'fixed-data-table';
import React from 'react';
import Relay from 'react-relay';
import { withRouter } from 'react-router';

import CellWithTooltip from '../../../shared/components/CellWithTooltip';
import ComparisonInfo from '../../../shared/components/ComparisonInfo';
import Table from '../../../shared/components/Table';
import getEntityTypeName from '../../../shared/getEntityTypeName';
import infoBlockStyles from '../../../shared/styles/infoBlocks.scss';

function getTypes({ viewer }) {
  const { comparison } = viewer;

  if (!comparison) {
    return null;
  }

  return comparison.entityTypes.map((entityType) => ({
    ...entityType,
    name: getEntityTypeName(entityType),
    addedEntities: entityType.addedEntities.totalCount,
    changedEntities: entityType.changedEntities.totalCount,
    deletedEntities: entityType.deletedEntities.totalCount,
  }));
}

class Comparison extends React.Component {
  static propTypes = {
    router: React.PropTypes.object.isRequired,
    viewer: React.PropTypes.shape({
      comparison: React.PropTypes.shape({
        entityTypes: React.PropTypes.arrayOf(React.PropTypes.shape({
          localId: React.PropTypes.number.isRequired,
          typeId: React.PropTypes.number.isRequired,
          oldName: React.PropTypes.string,
          newName: React.PropTypes.string,
          addedEntities: React.PropTypes.shape({
            totalCount: React.PropTypes.number.isRequired,
          }).isRequired,
          changedEntities: React.PropTypes.shape({
            totalCount: React.PropTypes.number.isRequired,
          }).isRequired,
          deletedEntities: React.PropTypes.shape({
            totalCount: React.PropTypes.number.isRequired,
          }).isRequired,
        }).isRequired).isRequired,
      }),
    }).isRequired,
  };
  _types = getTypes(this.props);

  componentWillReceiveProps(nextProps) {
    this._types = getTypes(nextProps);
  }

  _handleRowClick = (_, rowIndex) => {
    this.props.router.push(`/entity-type-${this._types[rowIndex].localId}`);
  };

  render() {
    const { comparison } = this.props.viewer;

    if (!comparison) {
      return <div>Comparison is not found.</div>;
    }

    const types = this._types;

    return (
      <div>
        <div className={infoBlockStyles.infoBlocks}>
          <ComparisonInfo comparison={comparison} />
        </div>

        {types.length ?
          <Table columnGroups onRowClick={this._handleRowClick} rowsCount={types.length}>
            <ColumnGroup header={<Cell>Entity type</Cell>}>
              <Column
                header={<Cell>Id</Cell>}
                cell={({ rowIndex }) => <Cell>{types[rowIndex].typeId}</Cell>}
                width={100}
              />
              <Column
                header={<Cell>Name</Cell>}
                cell={({ rowIndex }) => <CellWithTooltip>{types[rowIndex].name}</CellWithTooltip>}
                width={300}
              />
            </ColumnGroup>
            <ColumnGroup header={<Cell>Entities</Cell>}>
              <Column
                header={<Cell>Added</Cell>}
                cell={({ rowIndex }) => <Cell>{types[rowIndex].addedEntities}</Cell>}
                width={100}
              />
              <Column
                header={<Cell>Changed</Cell>}
                cell={({ rowIndex }) => <Cell>{types[rowIndex].changedEntities}</Cell>}
                width={100}
              />
              <Column
                header={<Cell>Deleted</Cell>}
                cell={({ rowIndex }) => <Cell>{types[rowIndex].deletedEntities}</Cell>}
                width={100}
              />
            </ColumnGroup>
          </Table> :
          <div>The contents of the stores are identical.</div>
        }
      </div>
    );
  }
}

export default Relay.createContainer(withRouter(Comparison), {
  initialVariables: {
    localId: null,
  },
  fragments: {
    viewer: () => Relay.QL`
      fragment on Viewer {
        comparison(localId: $localId) {
          ${ComparisonInfo.getFragment('comparison')}
          entityTypes {
            localId
            typeId
            oldName
            newName
            addedEntities {
              totalCount
            }
            changedEntities {
              totalCount
            }
            deletedEntities {
              totalCount
            }
          }
        }
      }
    `,
  },
});
