import { Table as FixedDataTable } from 'fixed-data-table';
import React from 'react';

import DimensionsInjector from './DimensionsInjector';
import styles from '../../styles/Table.scss';

const HEADER_HEIGHT = 30;
const ROW_HEIGHT = 50;
const SCROLLBAR_AND_BORDERS = 17;

export default class Table extends React.Component {
  static propTypes = {
    children: React.PropTypes.node.isRequired,
    columnGroups: React.PropTypes.bool,
  };

  render() {
    const { children, columnGroups, ...props } = this.props;

    const height = (columnGroups ? 2 : 1) * HEADER_HEIGHT + props.rowsCount * ROW_HEIGHT + SCROLLBAR_AND_BORDERS;

    return (
      <DimensionsInjector style={{ height: `${height}px` }}>
        <FixedDataTable
          groupHeaderHeight={columnGroups ? HEADER_HEIGHT : 0}
          headerHeight={HEADER_HEIGHT}
          height={0}
          rowHeight={ROW_HEIGHT}
          rowClassNameGetter={props.onRowClick && (() => styles.row)}
          width={0}
          {...props}
        >
          {children}
        </FixedDataTable>
      </DimensionsInjector>
    );
  }
}
