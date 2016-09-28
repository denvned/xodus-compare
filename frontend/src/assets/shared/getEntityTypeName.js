export default function getEntityTypeName({ oldName, newName }) {
  return oldName === newName || !oldName || !newName ? oldName || newName : `${newName} (${oldName})`;
}
