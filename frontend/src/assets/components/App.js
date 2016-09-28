import React from 'react';
import { withRouter } from 'react-router';
import AppBar from 'react-toolbox/lib/app_bar';

class App extends React.Component {
    static propTypes = {
      children: React.PropTypes.node.isRequired,
      router: React.PropTypes.object.isRequired,
    };

    render() {
        const { children, router } = this.props;

        return (
          <div>
            <AppBar leftIcon="home" onLeftIconClick={() => { router.push('/'); }} title="Xodus Compare" />
            <div style={{ padding: '16px' }}>
              {children}
            </div>
          </div>
        );
    }
}

export default withRouter(App)
