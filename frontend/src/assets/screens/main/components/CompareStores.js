import React from 'react';
import Relay from 'react-relay';
import { Button } from 'react-toolbox/lib/button';
import Dialog from 'react-toolbox/lib/dialog';

import CompareStoreMutation from '../mutations/CompareStoreMutation';
import StoreLocationInput from './StoreLocationInput';

const FORM_ID = 'da8dc7d3-2f3b-4536-9f8c-dddb8cb2b499';
const INITIAL_STATE = {
  open: false,
  oldStoreLocation: { dir: '', storeName: '' },
  newStoreLocation: { dir: '', storeName: '' },
  submitting: false,
};

class CompareStores extends React.PureComponent {
  static propTypes = {
    relay: React.PropTypes.object.isRequired,
    viewer: React.PropTypes.object.isRequired,
  };
  state = INITIAL_STATE;

  _handleOpen = () => {
    this.setState({ open: true });
  };

  _handleClose = () => {
      this.setState(INITIAL_STATE);
  };

  _handleSubmit = (event) => {
    const { relay, viewer } = this.props;
    const { oldStoreLocation, newStoreLocation } = this.state;

    event.preventDefault();

    this.setState({ submitting: true });

    relay.commitUpdate(
      new CompareStoreMutation({ oldStoreLocation, newStoreLocation, viewer }),
      {
        onSuccess: () => {
          this.setState(INITIAL_STATE);
        },
        onFailure: () => {
          this.setState({ submitting: false });
        },
      }
    );
  };

  _handleOldStoreLocationChange = (oldStoreLocation) => {
    this.setState({ oldStoreLocation });
  };

  _handleNewStoreLocationChange = (newStoreLocation) => {
    this.setState({ newStoreLocation });
  };

  render() {
    const { open, oldStoreLocation, newStoreLocation, submitting } = this.state;

    const actions = [
      { disabled: submitting, icon: "cancel", label: "Cancel", onClick: this._handleClose },
      { disabled: submitting, form: FORM_ID, icon: "compare_arrows", label: "Compare", type: 'submit' }
    ];

    return (
      <div>
        <Button icon="compare_arrows" label="New comparison…" onClick={this._handleOpen} />

        <Dialog
          actions={actions}
          active={open}
          onEscKeyDown={this._handleClose}
          onOverlayClick={this._handleClose}
          title="Compare Stores"
        >
          <form id={FORM_ID} onSubmit={this._handleSubmit}>
            <fieldset>
              <legend>Old store location:</legend>
              <StoreLocationInput
                disabled={submitting}
                location={oldStoreLocation}
                name="old"
                onChange={this._handleOldStoreLocationChange}
              />
            </fieldset>
            <fieldset>
              <legend>New store location:</legend>
              <StoreLocationInput
                disabled={submitting}
                location={newStoreLocation}
                name="new"
                onChange={this._handleNewStoreLocationChange}
              />
            </fieldset>
          </form>
        </Dialog>
      </div>
    );
  }
}

export default Relay.createContainer(CompareStores, {
  fragments: {
    viewer: () => Relay.QL`
      fragment on Viewer {
        ${CompareStoreMutation.getFragment('viewer')}
      }
    `,
  },
});
