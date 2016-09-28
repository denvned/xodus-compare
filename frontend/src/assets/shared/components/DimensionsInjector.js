import React from 'react';

export default class DimensionsInjector extends React.Component {
  static propTypes = {
    children: React.PropTypes.element.isRequired,
  };
  state = {
    width: 0,
    height: 0,
  };

  componentDidMount() {
    this._mounted = true;

    this._update();
  }

  componentWillUnmount() {
    this._mounted = false;
  }

  _setElement = (element) => {
    this._element = element;
  };

  _update = () => {
    if (this._mounted) {
      if (this._element) {
        const { width, height } = this._element.getBoundingClientRect();

        if (width !== this.state.width || height !== this.state.height) {
          this.setState({ width, height });
        }
      }

      window.requestAnimationFrame(this._update);
    }
  };

  render() {
    const { children, ...props } = this.props;
    const { width, height } = this.state;

    return (
      <div {...props} ref={this._setElement}>
        {React.cloneElement(children, { width, height })}
      </div>
    );
  }
}
