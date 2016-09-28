import { Cell, Column, ColumnGroup } from 'fixed-data-table';
import React from 'react';
import Relay from 'react-relay';
import { Tab, Tabs } from 'react-toolbox';

import CellWithTooltip from '../../../shared/components/CellWithTooltip';
import ComparisonInfo from '../../../shared/components/ComparisonInfo';
import EntityInfo from '../../../shared/components/EntityInfo';
import EntityTypeInfo from '../../../shared/components/EntityTypeInfo';
import Table from '../../../shared/components/Table';
import getEntityTypeName from '../../../shared/getEntityTypeName';
import infoBlockStyles from '../../../shared/styles/infoBlocks.scss';

function getInitialTabIndex({ entity }) {
  const { properties, blobs, links } = entity;

  if (properties.length) {
    return 0;
  }
  if (blobs.length) {
    return 1;
  }
  if (links.length) {
    return 2;
  }
  return 0;
}

function getLinks({ entity }) {
  return [].concat(...entity.links.map(({ name, targetTypes }) => targetTypes.map(
    ({ entityType, addedTargets, deletedTargets }) => ({
      linkName: name,
      targetEntityTypeName: getEntityTypeName(entityType),
      addedTargets: addedTargets.totalCount,
      deletedTargets: deletedTargets.totalCount,
    })
  )));
}

function getSize(size) {
  return size !== null ? size : 'Null';
}

class Entity extends React.Component {
  static propTypes = {
    entity: React.PropTypes.shape({
      localId: React.PropTypes.number.isRequired,
      properties: React.PropTypes.arrayOf(React.PropTypes.shape({
        name: React.PropTypes.string.isRequired,
        oldValueType: React.PropTypes.string,
        oldValue: React.PropTypes.string,
        newValueType: React.PropTypes.string,
        newValue: React.PropTypes.string,
      }).isRequired).isRequired,
      blobs: React.PropTypes.arrayOf(React.PropTypes.shape({
        name: React.PropTypes.string.isRequired,
        oldSize: React.PropTypes.number,
        newSize: React.PropTypes.number,
      }).isRequired).isRequired,
      links: React.PropTypes.arrayOf(React.PropTypes.shape({
        name: React.PropTypes.string.isRequired,
        targetTypes: React.PropTypes.arrayOf(React.PropTypes.shape({
          entityType: React.PropTypes.shape({
            oldName: React.PropTypes.string,
            newName: React.PropTypes.string,
          }).isRequired,
          addedTargets: React.PropTypes.shape({
            totalCount: React.PropTypes.number.isRequired,
          }).isRequired,
          deletedTargets: React.PropTypes.shape({
            totalCount: React.PropTypes.number.isRequired,
          }).isRequired,
        }).isRequired).isRequired,
      }).isRequired).isRequired,
      type: React.PropTypes.shape({
        comparison: React.PropTypes.object.isRequired,
      }),
    }).isRequired,
  };
  _links = getLinks(this.props);
  state = {
    tabIndex: getInitialTabIndex(this.props),
  };

  componentWillReceiveProps(nextProps) {
    if (nextProps.entity.localId !== this.props.entity.localId) {
      _links = getLinks(nextProps);
      this.setState({ tabIndex: getInitialTabIndex(nextProps) });
    }
  }

  _handleTabChange = (tabIndex) => {
    this.setState({ tabIndex });
  };

  render() {
    const { entity } = this.props;
    const { properties, blobs, type } = entity;

    return (
      <div>
        <div className={infoBlockStyles.infoBlocks}>
          <ComparisonInfo comparison={type.comparison} />
          <EntityTypeInfo entityType={type} />
          <EntityInfo entity={entity} />
        </div>

        <Tabs fixed index={this.state.tabIndex} onChange={this._handleTabChange}>
          <Tab disabled={!properties.length} label={`Changed properties (${properties.length})`}>
            <Table columnGroups rowsCount={properties.length}>
              <ColumnGroup header={<Cell>Name</Cell>}>
                <Column
                  cell={({ rowIndex }) => <CellWithTooltip>{properties[rowIndex].name}</CellWithTooltip>}
                  width={200}
                />
              </ColumnGroup>
              <ColumnGroup header={<Cell>Old value</Cell>}>
                <Column
                  header={<Cell>Type</Cell>}
                  cell={({ rowIndex }) => <Cell>{properties[rowIndex].oldValueType || 'Null'}</Cell>}
                  width={100}
                />
                <Column
                  header={<Cell>Value</Cell>}
                  cell={({ rowIndex }) => <CellWithTooltip>{properties[rowIndex].oldValue}</CellWithTooltip>}
                  width={300}
                />
              </ColumnGroup>
              <ColumnGroup header={<Cell>New value</Cell>}>
                <Column
                  header={<Cell>Type</Cell>}
                  cell={({ rowIndex }) => <Cell>{properties[rowIndex].newValueType || 'Null'}</Cell>}
                  width={100}
                />
                <Column
                  header={<Cell>Value</Cell>}
                  cell={({ rowIndex }) => <CellWithTooltip>{properties[rowIndex].newValue}</CellWithTooltip>}
                  width={300}
                />
              </ColumnGroup>
            </Table>
          </Tab>
          <Tab disabled={!blobs.length} label={`Changed blobs (${blobs.length})`}>
            <Table rowsCount={blobs.length}>
              <Column
                header={<Cell>Name</Cell>}
                cell={({ rowIndex }) => <CellWithTooltip>{blobs[rowIndex].name}</CellWithTooltip>}
                width={200}
              />
              <Column
                header={<Cell>Old size</Cell>}
                cell={({ rowIndex }) => <Cell>{getSize(blobs[rowIndex].oldSize)}</Cell>}
                width={100}
              />
              <Column
                header={<Cell>New size</Cell>}
                cell={({ rowIndex }) => <Cell>{getSize(blobs[rowIndex].newSize)}</Cell>}
                width={100}
              />
            </Table>
          </Tab>
          <Tab disabled={!this._links.length} label={`Changed links (${this._links.length})`}>
            <Table columnGroups rowsCount={this._links.length}>
              <ColumnGroup header={<Cell>Link</Cell>}>
                <Column
                  header={<Cell>Name</Cell>}
                  cell={({ rowIndex }) => <CellWithTooltip>{this._links[rowIndex].linkName}</CellWithTooltip>}
                  width={200}
                />
              </ColumnGroup>
              <ColumnGroup header={<Cell>Target entity</Cell>}>
                <Column
                  header={<Cell>Type</Cell>}
                  cell={({ rowIndex }) =>
                    <CellWithTooltip>{this._links[rowIndex].targetEntityTypeName}</CellWithTooltip>
                  }
                  width={200}
                />
                <Column
                  header={<Cell>Added</Cell>}
                  cell={({ rowIndex }) => <Cell>{this._links[rowIndex].addedTargets}</Cell>}
                  width={100}
                />
                <Column
                  header={<Cell>Deleted</Cell>}
                  cell={({ rowIndex }) => <Cell>{this._links[rowIndex].deletedTargets}</Cell>}
                  width={100}
                />
              </ColumnGroup>
            </Table>
          </Tab>
        </Tabs>
      </div>
    );
  }
}

export default Relay.createContainer(Entity, {
  fragments: {
    entity: () => Relay.QL`
      fragment on Entity {
        localId
        properties {
          name
          oldValueType
          oldValue
          newValueType
          newValue
        }
        blobs {
          name
          oldSize
          newSize
        }
        links {
          name
          targetTypes {
            entityType {
              oldName
              newName
            }
            addedTargets {
              totalCount
            }
            deletedTargets {
              totalCount
            }
          }
        }
        type {
          comparison {
            ${ComparisonInfo.getFragment('comparison')}
          }
          ${EntityTypeInfo.getFragment('entityType')}
        }
        ${EntityInfo.getFragment('entity')}
      }
    `,
  },
});
