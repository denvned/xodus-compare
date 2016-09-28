import React from 'react';
import Input from 'react-toolbox/lib/input';

export default class StoreLocationInput extends React.PureComponent {
  static propTypes = {
    disabled: React.PropTypes.bool.isRequired,
    location: React.PropTypes.shape({
      dir: React.PropTypes.string.isRequired,
      storeName: React.PropTypes.string.isRequired,
    }).isRequired,
    name: React.PropTypes.string.isRequired,
    onChange: React.PropTypes.func.isRequired,
  };

  _handleDirChange = (dir) => {
    const { location, onChange } = this.props;

    onChange({
      dir,
      storeName: location.storeName,
    });
  };

  _handleStoreNameChange = (storeName) => {
    const { location, onChange } = this.props;

    onChange({
      dir: location.dir,
      storeName,
    });
  };

  render() {
    const { disabled, location, name } = this.props;

    return (
      <div>
        <Input
          disabled={disabled}
          icon="folder"
          label="Path"
          name={`${name}StoreDir`}
          onChange={this._handleDirChange}
          required
          type="text"
          value={location.dir}
        />
        <Input
          disabled={disabled}
          icon="label"
          label="Store name"
          name={`${name}StoreName`}
          onChange={this._handleStoreNameChange}
          required
          type="text"
          value={location.storeName}
        />
      </div>
    );
  }
}
