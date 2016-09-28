import React from 'react';
import Relay from 'react-relay';
import { Tab, Tabs } from 'react-toolbox';

import ComparisonInfo from '../../../shared/components/ComparisonInfo';
import EntityTypeInfo from '../../../shared/components/EntityTypeInfo';
import infoBlockStyles from '../../../shared/styles/infoBlocks.scss';
import { AddedEntities, ChangedEntities, DeletedEntities } from './Entities';

const ENTITIES_PER_PAGE = 20;

function getInitialTabIndex({ viewer }) {
  const { entityType } = viewer;
  if (!entityType) {
    return 0;
  }

  const { addedEntities, changedEntities, deletedEntities } = viewer.entityType;
  if (addedEntities.totalCount) {
    return 0;
  }
  if (changedEntities.totalCount) {
    return 1;
  }
  if (deletedEntities.totalCount) {
    return 2;
  }
  return 0;
}

class EntityType extends React.Component {
  static propTypes = {
    relay: React.PropTypes.object.isRequired,
    viewer: React.PropTypes.shape({
      entityType: React.PropTypes.shape({
        localId: React.PropTypes.number.isRequired,
        comparison: React.PropTypes.object.isRequired,
        addedEntities: React.PropTypes.shape({
          totalCount: React.PropTypes.number.isRequired,
        }).isRequired,
        changedEntities: React.PropTypes.shape({
          totalCount: React.PropTypes.number.isRequired,
        }).isRequired,
        deletedEntities: React.PropTypes.shape({
          totalCount: React.PropTypes.number.isRequired,
        }).isRequired,
      }),
    }).isRequired,
  };
  state = {
    tabIndex: getInitialTabIndex(this.props),
  };

  componentWillReceiveProps(nextProps) {
    const prevType = this.props.viewer.entityType;
    const nextType = nextProps.viewer.entityType;

    if (nextType && (!prevType || nextType.localId !== prevType.localId)) {
      this.setState({ tabIndex: getInitialTabIndex(nextProps) });
    }
  }

  _handleLoadMoreEntities(name) {
    const { relay } = this.props;
    const varName = `${name}EntitiesLimit`;
    relay.setVariables({ [varName]: relay.variables[varName] + ENTITIES_PER_PAGE });
  };

  _handleTabChange = (tabIndex) => {
    this.setState({ tabIndex });
  };

  render() {
    const { entityType } = this.props.viewer;

    if (!entityType) {
      return <div>Entity type is not found.</div>;
    }

    const { comparison, addedEntities, changedEntities, deletedEntities } = entityType;

    return (
      <div>
        <div className={infoBlockStyles.infoBlocks}>
          <ComparisonInfo comparison={comparison} />
          <EntityTypeInfo entityType={entityType} />
        </div>

        <Tabs fixed index={this.state.tabIndex} onChange={this._handleTabChange}>
          <Tab disabled={!addedEntities.totalCount} label={`Added entities (${addedEntities.totalCount})`}>
            <AddedEntities entities={addedEntities} onLoadMore={() => this._handleLoadMoreEntities('added')} />
          </Tab>
          <Tab disabled={!changedEntities.totalCount} label={`Changed entities (${changedEntities.totalCount})`}>
            <ChangedEntities entities={changedEntities} onLoadMore={() => this._handleLoadMoreEntities('changed')} />
          </Tab>
          <Tab disabled={!deletedEntities.totalCount} label={`Deleted entities (${deletedEntities.totalCount})`}>
            <DeletedEntities entities={deletedEntities} onLoadMore={() => this._handleLoadMoreEntities('deleted')} />
          </Tab>
        </Tabs>
      </div>
    );
  }
}

export default Relay.createContainer(EntityType, {
  initialVariables: {
    localId: null,
    addedEntitiesLimit: ENTITIES_PER_PAGE,
    changedEntitiesLimit: ENTITIES_PER_PAGE,
    deletedEntitiesLimit: ENTITIES_PER_PAGE,
  },
  fragments: {
    viewer: () => Relay.QL`
      fragment on Viewer {
        entityType(localId: $localId) {
          localId
          comparison {
            ${ComparisonInfo.getFragment('comparison')}
          }
          addedEntities(first: $addedEntitiesLimit) {
            totalCount
            ${AddedEntities.getFragment('entities')}
          }
          changedEntities(first: $changedEntitiesLimit) {
            totalCount
            ${ChangedEntities.getFragment('entities')}
          }
          deletedEntities(first: $deletedEntitiesLimit) {
            totalCount
            ${DeletedEntities.getFragment('entities')}
          }
          ${EntityTypeInfo.getFragment('entityType')}
        }
      }
    `,
  },
});
