export default function getEntityUrl({ __typename, localId }) {
  let type;
  switch (__typename) {
    case 'AddedEntity':
      type = 'added';
      break;
    case 'ChangedEntity':
      type = 'changed';
      break;
    case 'DeletedEntity':
      type = 'deleted';
      break;
    default:
      throw Error(`Unknown entity change type: ${__typename}`);
  }

  return `/${type}-entity-${localId}`;
}
