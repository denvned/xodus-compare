import { Cell, Column, ColumnGroup } from 'fixed-data-table';
import React from 'react';
import Relay from 'react-relay';
import { withRouter } from 'react-router';
import { Button } from 'react-toolbox/lib/button';

import Table from '../../../shared/components/Table';
import getEntityUrl from '../../../shared/getEntityUrl';

function getEntities({ entities }) {
  return entities.edges.map(({ node }) => node);
}

class Entities extends React.Component {
  static propTypes = {
    entities: React.PropTypes.shape({
      edges: React.PropTypes.arrayOf(React.PropTypes.shape({
        node: React.PropTypes.shape({
          __typename: React.PropTypes.oneOf(['AddedEntity', 'ChangedEntity', 'DeletedEntity']).isRequired,
          localId: React.PropTypes.number.isRequired,
          entityId: React.PropTypes.number.isRequired,
          propertyCount: React.PropTypes.number.isRequired,
          blobCount: React.PropTypes.number.isRequired,
          linkCount: React.PropTypes.number.isRequired,
        }).isRequired,
      }).isRequired).isRequired,
      pageInfo: React.PropTypes.shape({
        hasNextPage: React.PropTypes.bool.isRequired,
      }).isRequired,
    }).isRequired,
    onLoadMore: React.PropTypes.func.isRequired,
    router: React.PropTypes.object.isRequired,
  };
  _entities = getEntities(this.props);

  componentWillReceiveProps(nextProps) {
    this._entities = getEntities(nextProps);
  }

  _handleRowClick = (_, rowIndex) => {
    this.props.router.push(getEntityUrl(this._entities[rowIndex]));
  };

  render() {
    const { hasNextPage } = this.props.entities.pageInfo;

    return (
      <div>
        <Table columnGroups onRowClick={this._handleRowClick} rowsCount={this._entities.length}>
          <ColumnGroup header={<Cell>Entity</Cell>}>
            <Column
              header={<Cell>Id</Cell>}
              cell={({ rowIndex }) => <Cell>{this._entities[rowIndex].entityId}</Cell>}
              width={100}
            />
          </ColumnGroup>
          <ColumnGroup header={<Cell>Changed</Cell>}>
            <Column
              header={<Cell>Properties</Cell>}
              cell={({ rowIndex }) => <Cell>{this._entities[rowIndex].propertyCount}</Cell>}
              width={100}
            />
            <Column
              header={<Cell>Blobs</Cell>}
              cell={({ rowIndex }) => <Cell>{this._entities[rowIndex].blobCount}</Cell>}
              width={100}
            />
            <Column
              header={<Cell>Links</Cell>}
              cell={({ rowIndex }) => <Cell>{this._entities[rowIndex].linkCount}</Cell>}
              width={100}
            />
          </ColumnGroup>
        </Table>
        {hasNextPage && <Button label="Load more…" onClick={this.props.onLoadMore} />}
      </div>
    );
  }
}

const EntitiesWithRouter = withRouter(Entities);

export const AddedEntities = Relay.createContainer(EntitiesWithRouter, {
  fragments: {
    entities: () => Relay.QL`
      fragment on AddedEntityConnection {
        edges {
          node {
            __typename
            localId
            entityId
            propertyCount
            blobCount
            linkCount
          }
        }
        pageInfo {
          hasNextPage
        }
      }
    `,
  },
});

export const ChangedEntities = Relay.createContainer(EntitiesWithRouter, {
  fragments: {
    entities: () => Relay.QL`
      fragment on ChangedEntityConnection {
        edges {
          node {
            __typename
            localId
            entityId
            propertyCount
            blobCount
            linkCount
          }
        }
        pageInfo {
          hasNextPage
        }
      }
    `,
  },
});

export const DeletedEntities = Relay.createContainer(EntitiesWithRouter, {
  fragments: {
    entities: () => Relay.QL`
      fragment on DeletedEntityConnection {
        edges {
          node {
            __typename
            localId
            entityId
            propertyCount
            blobCount
            linkCount
          }
        }
        pageInfo {
          hasNextPage
        }
      }
    `,
  },
});
